package com.kiaev.dealer.login;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 딜러 로그인 엔티티
 * 
 * dealer_tbl 테이블과 매핑됩니다. 로그인 처리 및 세션 저장에 사용하는 딜러 기본 정보입니다.
 */
@Entity
@Table(name = "dealer_tbl")
public class DealerLogin {

	// 딜러번호(PK)
	@Id
	@Column(name = "dealer_no")
	private Integer dealerNo;

	// 사원번호
	@Column(name = "dealer_emp_no", nullable = false, unique = true, length = 30)
	private String dealerEmpNo;

	// 비밀번호
	// 현재 컬럼 길이는 255로 유지
	// → 입력값은 Service / HTML에서 8~20자로 제한
	@Column(name = "dealer_pw", nullable = false, length = 255)
	private String dealerPw;

	// 딜러명
	@Column(name = "dealer_name", nullable = false, length = 50)
	private String dealerName;

	// 생년월일
	@Column(name = "birth_date", nullable = false)
	private LocalDate birthDate;

	// 연락처
	@Column(name = "phone", nullable = false, length = 20)
	private String phone;

	// 이메일
	@Column(name = "email", length = 100)
	private String email;

	// 소속 지점
	@Column(name = "affiliation", nullable = false, length = 100)
	private String affiliation;

	// 계정 상태
	// 예: ACTIVE / INACTIVE / 퇴사
	@Column(name = "dealer_status", nullable = false, length = 20)
	private String dealerStatus;

	// 승인 상태
	// 예: WAIT / APPROVED / REJECTED
	@Column(name = "approval_status", nullable = false, length = 20)
	private String approvalStatus;

	// 기본 생성자
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