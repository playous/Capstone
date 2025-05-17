package capstone.demo.service;

import capstone.demo.domain.Document;
import capstone.demo.domain.SecurityElement;
import capstone.demo.domain.User;
import capstone.demo.dto.ResultListDto;
import capstone.demo.dto.ResultDto;
import capstone.demo.enums.DocumentStatus;
import capstone.demo.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    private final S3Service s3Service;

    @Transactional
    public Document uploadDocument(MultipartFile file, User user, String title) throws IOException {
        // S3에 파일 업로드
        String s3Key = s3Service.uploadFile(file,user);

        // Document 객체 생성 및 저장
        Document document = new Document();
        document.setUser(user);
        document.setS3Key(s3Key);
        document.setOriginalFilename(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setTitle(title);
        document.setStatus(DocumentStatus.PROCESSING);

        return documentRepository.save(document);
    }

    @Transactional
    public boolean deleteDocument(Long documentId, User user){
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다. ID: " + documentId));

        s3Service.deleteFile(document.getS3Key());

        documentRepository.delete(document);

        return true;
    }


    public int getDocumentsBySecurityLevel(int level,Long userId) {
        return documentRepository.findBySecurityLevel(level, userId);
    }

    public Page<Document> getDocumentsByUser(Long userId, Pageable pageable) {
        return documentRepository.findByUserId(userId, pageable);
    }

    public Page<Document> getDocumentsBySecurityLevel(Integer level, Long userId, Pageable pageable) {
        return documentRepository.findBySecurityLevel(level, userId, pageable);
    }

    public Page<Document> searchDocumentsByTitle(String keyword, Long userId, Pageable pageable) {
        return documentRepository.findByTitleContaining(keyword, userId, pageable);
    }


    public Document getDocument(Long id) {
        return documentRepository.findByIdWithSecurityElements(id);
    }

    public void saveDocumentAnalysisResults(Long id, ResultListDto resultListDto) {
        Document document = documentRepository.findByIdWithSecurityElements(id);

        Integer securityLevel = resultListDto.getDocumentSecurityLevel();
        document.setLevel(securityLevel);
        document.setStatus(DocumentStatus.COMPLETED);

        if (resultListDto.getResultList() != null) {
            for (ResultDto resultItem : resultListDto.getResultList()) {
                SecurityElement element = new SecurityElement();
                element.setDocument(document);
                element.setName(resultItem.getName());
                element.setLevel(resultItem.getLevel());
                element.setDescription(resultItem.getDescription());

                document.getSecurityElements().add(element);
            }
        }

        documentRepository.save(document);


    }

    public ResultListDto documentToDto(Document document) {
        ResultListDto resultListDto = new ResultListDto();
        resultListDto.setDocumentSecurityLevel(document.getLevel());

        List<ResultDto> resultList = new ArrayList<>();

        for (SecurityElement element : document.getSecurityElements()) {
            ResultDto resultDto = new ResultDto();
            resultDto.setName(element.getName());
            resultDto.setLevel(element.getLevel());
            resultDto.setDescription(element.getDescription());

            resultList.add(resultDto);
        }

        resultListDto.setResultList(resultList);

        return resultListDto;

    }
}
