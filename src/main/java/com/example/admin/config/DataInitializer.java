package com.example.admin.config;

import com.example.admin.model.entity.User;
import com.example.admin.model.enums.UserRole;
import com.example.admin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 앱 시작 시 초기 Admin 계정 생성 (없을 경우에만)
 * 초기 계정: admin@example.com / admin1234
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setName("관리자");
            admin.setRole(UserRole.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
            log.info("✅ 초기 Admin 계정 생성 완료: admin@example.com / admin1234");
        }

        if (userRepository.findByEmail("user@example.com").isEmpty()) {
            User user = new User();
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user1234"));
            user.setName("일반사용자");
            user.setRole(UserRole.USER);
            user.setActive(true);
            userRepository.save(user);
            log.info("✅ 초기 User 계정 생성 완료: user@example.com / user1234");
        }
    }
}
