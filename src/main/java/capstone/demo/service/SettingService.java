package capstone.demo.service;

import capstone.demo.domain.SecurityCriterion;
import capstone.demo.domain.User;
import capstone.demo.dto.CriteriaDto;
import capstone.demo.repository.SecurityCriterionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingService {

    private final SecurityCriterionRepository securityCriterionRepository;

    public List<CriteriaDto> getCriterionByUser(User user) {
        List<SecurityCriterion> criteria = securityCriterionRepository.findByUser(user);

        return criteria.stream()
                .map(criterion -> new CriteriaDto(
                        criterion.getCriterionName(),
                        criterion.getImportance()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveCriteria(User user, List<CriteriaDto> criteriaList) {

        log.info("{}의 보안 기준 : {}",user.getName(), criteriaList);

        if (criteriaList == null) {
            criteriaList = new ArrayList<>();
        }

        // 1. 보안 기준 초기화
        List<SecurityCriterion> existingCriteria = securityCriterionRepository.findByUser(user);
        securityCriterionRepository.deleteAll(existingCriteria);

        // 2. 새로운 기준들 저장
        for (CriteriaDto dto : criteriaList) {
            SecurityCriterion criterion = new SecurityCriterion();
            criterion.setUser(user);
            criterion.setCriterionName(dto.getName());
            criterion.setImportance(dto.getImportance());

            securityCriterionRepository.save(criterion);
        }
    }

}
