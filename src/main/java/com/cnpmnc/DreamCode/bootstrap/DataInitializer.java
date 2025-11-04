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

    @Value("${SEED_SAMPLE_USERS:true}")
    private boolean seedSampleUsers;

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

        if (seedSampleUsers) {
            if (!userRepository.existsByUserName("viewer1")) {
                User viewer = new User();
                viewer.setUserName("viewer1");
                viewer.setPassword(passwordEncoder.encode("viewer123"));
                viewer.setRoles(List.of("VIEWER"));
                userRepository.save(viewer);
            }
            if (!userRepository.existsByUserName("assetmgr1")) {
                User am = new User();
                am.setUserName("assetmgr1");
                am.setPassword(passwordEncoder.encode("asset123"));
                am.setRoles(List.of("ASSET_MANAGER"));
                userRepository.save(am);
            }
            if (!userRepository.existsByUserName("deptmgr1")) {
                User dm = new User();
                dm.setUserName("deptmgr1");
                dm.setPassword(passwordEncoder.encode("dept123"));
                dm.setRoles(List.of("DEPT_MANAGER"));
                userRepository.save(dm);
            }
            if (!userRepository.existsByUserName("user1")) {
                User u = new User();
                u.setUserName("user1");
                u.setPassword(passwordEncoder.encode("user123"));
                u.setRoles(List.of("USER"));
                userRepository.save(u);
            }
        }
    }
}
