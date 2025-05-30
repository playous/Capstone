package capstone.demo.service;

import capstone.demo.domain.Document;
import capstone.demo.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIAnalysisService {

    private final WebClient.Builder webClientBuilder;
    private final DocumentService documentService;
    private final S3Service s3Service;
    private final SettingService settingService;

    @Value("${api.ai.serviceUrl}")
    private String aiServiceUrl;

    @Value("${api.ai.endPoint}")
    private String aiEndpoint;

    public CompletableFuture<ResultListDto> analyzeDocument(Long documentId) {
        log.info("문서 보안 등급 시작 : {}", documentId);

        try {
            // 1. 문서 정보 조회
            Document document = documentService.getDocument(documentId);

            // 2. S3에서 미리 서명된 URL 생성 (30분 유효)
            String presignedUrl = s3Service.generatePresignedUrl(document.getS3Key(), 30);

            // 3. 사용자의 보안 기준 목록 조회
            List<CriteriaDto> securityCriteria =
                    settingService.getCriterionByUser(document.getUser());

            // 4. CriteriaListDto 생성
            CriteriaListDto criteriaListDto = new CriteriaListDto();
            criteriaListDto.setCriteriaList(securityCriteria);

            // 5. 분석 요청 DTO 생성
            AnalysisRequestDto requestDto = new AnalysisRequestDto();
            requestDto.setFileUrl(presignedUrl);
            requestDto.setCriterion(criteriaListDto);

            log.info("분석 요청 완료 => Id : {} , url : {}, criterion : {}", documentId, presignedUrl, criteriaListDto);

            log.info("전송할 JSON: {}", requestDto);

            // 6. WebClient로 AI 서비스 호출
            WebClient webClient = webClientBuilder
                    .baseUrl(aiServiceUrl)
                    .build();

            return webClient.post()
                    .uri(aiEndpoint)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> parseAndMapResponse(response, documentId))
                    .doOnSuccess(result -> {
                        log.info("분석 완료 : {}", documentId);
                        // 7. 분석 결과 저장
                        documentService.saveDocumentAnalysisResults(documentId, result);
                    })
                    .doOnError(error -> {
                        log.error("분석중 에러 발생 : {}", documentId, error);
                    })
                    .toFuture();
        } catch (Exception e) {
            log.error("분석중 에러 발생 : {}", documentId, e);
            CompletableFuture<ResultListDto> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
    private ResultListDto parseAndMapResponse(String aiResponse, Long documentId) {
        try {
            log.info("AI 응답 원본: {}", aiResponse);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseArray = objectMapper.readTree(aiResponse);

            // 배열에서 첫 번째 요소(문자열) 가져오기
            if (responseArray.isArray() && responseArray.size() > 0) {
                String fullText = responseArray.get(0).asText();

                // <think>...</think> 부분 제거하고 JSON만 추출
                int jsonStart = fullText.indexOf("{");
                int jsonEnd = fullText.lastIndexOf("}") + 1;

                if (jsonStart >= 0 && jsonEnd > jsonStart) {
                    String cleanJson = fullText.substring(jsonStart, jsonEnd);
                    log.info("정리된 JSON: {}", cleanJson);

                    // 실제 JSON 파싱
                    JsonNode jsonNode = objectMapper.readTree(cleanJson);

                    ResultListDto result = new ResultListDto();
                    result.setDocumentId(documentId);
                    result.setDocumentSecurityLevel(jsonNode.get("documentSecurityLevel").asInt());

                    // resultList 처리
                    List<ResultDto> resultList = new ArrayList<>();
                    JsonNode resultArray = jsonNode.get("resultList");

                    if (resultArray != null && resultArray.isArray()) {
                        for (JsonNode item : resultArray) {
                            ResultDto dto = new ResultDto();
                            dto.setName(item.get("name").asText());
                            dto.setLevel(item.get("level").asInt());
                            dto.setDescription(item.get("description").asText());
                            resultList.add(dto);
                        }
                    }

                    result.setResultList(resultList);
                    return result;
                }
            }

        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", e.getMessage());
        }

        // 실패 시 기본값 반환
        ResultListDto defaultResult = new ResultListDto();
        defaultResult.setDocumentId(documentId);
        defaultResult.setDocumentSecurityLevel(1); // 일반 등급

        List<ResultDto> defaultResultList = new ArrayList<>();
        ResultDto noSensitiveInfo = new ResultDto();
        noSensitiveInfo.setLevel(3);
        noSensitiveInfo.setName("분석 결과");
        noSensitiveInfo.setDescription("발견된 민감정보가 없습니다.");
        defaultResultList.add(noSensitiveInfo);

        defaultResult.setResultList(defaultResultList);
        return defaultResult;
    }
}