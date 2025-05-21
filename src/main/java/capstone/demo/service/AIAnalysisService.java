package capstone.demo.service;

import capstone.demo.domain.Document;
import capstone.demo.dto.AnalysisRequestDto;
import capstone.demo.dto.CriteriaDto;
import capstone.demo.dto.CriteriaListDto;
import capstone.demo.dto.ResultListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

    @Async
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
            requestDto.setDocumentId(documentId);
            requestDto.setFileUrl(presignedUrl);
            requestDto.setCriterion(criteriaListDto);

            log.info("분석 요청 완료 => Id : {} , url : {}, criterion : {}", documentId, presignedUrl, criteriaListDto);

            // 6. WebClient로 AI 서비스 호출
            WebClient webClient = webClientBuilder
                    .baseUrl(aiServiceUrl)
                    .build();

            return webClient.post()
                    .uri(aiEndpoint)
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(ResultListDto.class)
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
}