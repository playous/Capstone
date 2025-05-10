package capstone.demo.repository;

import capstone.demo.domain.SecurityCriterion;
import capstone.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecurityCriterionRepository extends JpaRepository<SecurityCriterion, Long> {

    List<SecurityCriterion> findByUser(User user);
}
