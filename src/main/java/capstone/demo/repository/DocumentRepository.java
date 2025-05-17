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

    @Query("SELECT COUNT(d) FROM Document d Where d.level = :level and d.user.id = :userId")
    int findBySecurityLevel(@Param("level") Integer level, @Param("userId") Long userId);

    @Query("SELECT d FROM Document d LEFT JOIN FETCH d.securityElements WHERE d.documentId = :id")
    Document findByIdWithSecurityElements(@Param("id") Long id);

    @Query("SELECT d FROM Document d WHERE d.level = :level AND d.user.id = :userId")
    Page<Document> findBySecurityLevel(@Param("level") Integer level, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.title LIKE %:keyword% AND d.user.id = :userId")
    Page<Document> findByTitleContaining(@Param("keyword") String keyword,@Param("userId") Long userId, Pageable pageable);

    Page<Document> findByUserId(Long userId, Pageable pageable);

}
