package capstone.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SecurityElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    private String name; // 요소명 (개인 신원정보, 재정 정보 등)

    private Integer level; // 해당 요소의 보안 등급 (1-4)

    private String description; // 설명
}