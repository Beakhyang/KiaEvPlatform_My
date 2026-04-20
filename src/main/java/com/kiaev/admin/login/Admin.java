package com.kiaev.admin.login;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ADMIN_TBL")
@Getter
@Setter
@NoArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_no")
    private Long adminNo;

    @Column(name = "admin_id", nullable = false, unique = true, length = 50)
    private String adminId;

    @Column(name = "admin_pw", nullable = false, length = 255)
    private String adminPw;

    @Column(name = "admin_name", nullable = false, length = 50)
    private String adminName;

    @Column(name = "admin_role", nullable = false, length = 20)
    private String adminRole = "SUPER_ADMIN";

    @Column(name = "admin_status", nullable = false, length = 20)
    private String adminStatus = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
