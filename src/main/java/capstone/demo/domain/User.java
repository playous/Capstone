package capstone.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String company;

    private String userId;

    private String password;

    private String email;

    @OneToMany(mappedBy = "user")
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecurityCriterion> securityCriteria = new ArrayList<>();

    // 보안 기준 추가 메서드
    public void addSecurityCriteria(SecurityCriterion criteria) {
        securityCriteria.add(criteria);
        criteria.setUser(this);
    }
}
