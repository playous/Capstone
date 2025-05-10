package capstone.demo.service;

import capstone.demo.domain.SecurityCriterion;
import capstone.demo.domain.User;
import capstone.demo.dto.CreateUserDto;
import capstone.demo.repository.SecurityCriterionRepository;
import capstone.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityCriterionRepository securityCriterionRepository;


    @Transactional
    public long saveUser(CreateUserDto createUserDto) {
        User user = dtoToUser(createUserDto);
        userRepository.save(user);

        List<SecurityCriterion> defaultCriteria = Arrays.asList(
                createCriterion(user, "개인정보", 3),
                createCriterion(user, "내부 행사", 1)
        );

        securityCriterionRepository.saveAll(defaultCriteria);
        return user.getId();
    }

    @Transactional(readOnly = true)
    public boolean isUserIdDuplicate(String userId) {
        User existingUser = userRepository.findUserByUserId(userId);
        if (existingUser != null) log.error("중복 ID = {}", userId);
        return existingUser != null;
    }

    @Transactional(readOnly = true)
    public User authenticate(String userId, String password) {
        User user = userRepository.findUserByUserId(userId);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;

    }

    public User dtoToUser(CreateUserDto createUserDto) {
        User user = new User();
        user.setName(createUserDto.getName());
        user.setCompany(createUserDto.getCompany());
        user.setUserId(createUserDto.getUserId());
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword())); //암호화
        user.setEmail(createUserDto.getEmail());
        return user;
    }

    public long getUserId(CreateUserDto createUserDto) {
        String userId = createUserDto.getUserId();
        User user = userRepository.findUserByUserId(userId);
        return user.getId();
    }

    private SecurityCriterion createCriterion(User user, String name, Integer importance) {
        SecurityCriterion criterion = new SecurityCriterion();
        criterion.setUser(user);
        criterion.setCriterionName(name);
        criterion.setImportance(importance);
        return criterion;
    }

}
