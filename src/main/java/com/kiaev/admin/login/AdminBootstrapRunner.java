package com.kiaev.admin.login;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminBootstrapRunner implements CommandLineRunner {

    private final AdminRepository adminRepository;

    @Override
    public void run(String... args) {
        if (adminRepository.count() > 0) {
            return;
        }

        Admin admin = new Admin();
        admin.setAdminId("gmadmin");
        admin.setAdminPw("admin1234");
        admin.setAdminName("GM");
        admin.setAdminRole("SUPER_ADMIN");
        admin.setAdminStatus("ACTIVE");
        adminRepository.save(admin);
    }
}
