package com.kiaev.admin.login;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public Admin login(String adminId, String adminPw) {
        if (adminId == null || adminId.isBlank() || adminPw == null) {
            return null;
        }

        Optional<Admin> optionalAdmin = adminRepository.findByAdminId(adminId.trim());
        if (optionalAdmin.isEmpty()) {
            return null;
        }

        Admin admin = optionalAdmin.get();
        if (!"ACTIVE".equalsIgnoreCase(admin.getAdminStatus())) {
            return null;
        }

        if (!adminPw.equals(admin.getAdminPw())) {
            return null;
        }

        return admin;
    }

    public long countAdmins() {
        return adminRepository.count();
    }
}
