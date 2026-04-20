package com.kiaev.dealer.login;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dealer_tbl")
public class DealerLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dealer_no")
    private Integer dealerNo;

    @Column(name = "dealer_emp_no", nullable = false, unique = true, length = 30)
    private String dealerEmpNo;

    @Column(name = "dealer_pw", nullable = false, length = 255)
    private String dealerPw;

    @Column(name = "dealer_name", nullable = false, length = 50)
    private String dealerName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "affiliation", nullable = false, length = 100)
    private String affiliation;

    @Column(name = "dealer_status", nullable = false, length = 20)
    private String dealerStatus;

    @Column(name = "approval_status", nullable = false, length = 20)
    private String approvalStatus;

    public DealerLogin() {
    }

    public Integer getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(Integer dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getDealerEmpNo() {
        return dealerEmpNo;
    }

    public void setDealerEmpNo(String dealerEmpNo) {
        this.dealerEmpNo = dealerEmpNo;
    }

    public String getDealerPw() {
        return dealerPw;
    }

    public void setDealerPw(String dealerPw) {
        this.dealerPw = dealerPw;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getDealerStatus() {
        return dealerStatus;
    }

    public void setDealerStatus(String dealerStatus) {
        this.dealerStatus = dealerStatus;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
