package capstone.demo.domain;

import capstone.demo.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String s3Key;              // S3내 파일 경로
    private String originalFilename;   // 원본 파일명
    private String contentType;        // 콘텐츠 타입
    private Long fileSize;             // 파일 크기 (바이트)

    private Integer level; // 보안 등급 (1-4)

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<SecurityElement> securityElements = new ArrayList<>();

    // 문서 제목
    private String title;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status; // PROCESSING, COMPLETED

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
