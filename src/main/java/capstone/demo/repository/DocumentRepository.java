package capstone.demo.repository;

import capstone.demo.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("SELECT COUNT(d) FROM Document d Where d.level = :level")
    int findBySecurityLevel(@Param("level") Integer level);

    @Query("SELECT d FROM Document d LEFT JOIN FETCH d.securityElements WHERE d.documentId = :id")
    Document findByIdWithSecurityElements(@Param("id") Long id);

    Document findByDocumentId(Long documentId);

    // 최근 문서를 createdAt 기준으로 정렬하여 가져오기
    Page<Document> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
