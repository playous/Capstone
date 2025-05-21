package capstone.demo.service;

import capstone.demo.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client amazonS3;

    private final S3Presigner amazonS3Presigner;

    @Value("${app.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, User user) throws IOException {
        String s3Key = "documents/" + user.getId() + "/" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                "_" + file.getOriginalFilename().replaceAll("\\s+", "_");

        String contentType = file.getContentType();

        if (contentType.startsWith("text/")) {
            contentType += "; charset=UTF-8";
        }
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        amazonS3.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return s3Key;
    }

    public String generatePresignedUrl(String s3Key, long expirationTime) {
        // GetObjectRequest 생성
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        // GetObjectPresignRequest 생성
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationTime))
                .getObjectRequest(getObjectRequest)
                .build();

        // Presigned URL 생성
        PresignedGetObjectRequest presignedRequest = amazonS3Presigner.presignGetObject(presignRequest);

        // URL 반환
        return presignedRequest.url().toString();
    }

    public boolean deleteFile(String s3Key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        amazonS3.deleteObject(deleteObjectRequest);
        return true;
    }
}