package com.cnpmnc.DreamCode.bootstrap;

import com.cnpmnc.DreamCode.model.User;
import com.cnpmnc.DreamCode.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUserName(adminUsername)) {
            User admin = new User();
            admin.setUserName(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRoles(List.of("ADMIN"));
            userRepository.save(admin);
        }
    }
}
