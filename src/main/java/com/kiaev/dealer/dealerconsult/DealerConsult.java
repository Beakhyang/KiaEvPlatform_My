package com.kiaev.dealer.dealerconsult;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * 상담 엔티티
 * 
 * consult_tbl 테이블과 매핑됩니다. 딜러가 담당하는 상담 정보 1건을 의미합니다.
 * 
 * 주의: - 현재 프로젝트에서는 차량번호(car_no)를 문자열(String)로 처리합니다. - 예: "123가1111"
 * 
 * 추가 설명: - consult_memo 는 고객 / 관리자 측에서 작성한 상담 관련 메모를 저장하는 공용 컬럼입니다. - 딜러 화면에서는
 * 이 값을 "조회 전용"으로만 사용합니다. - consult_memo 에 여러 줄 텍스트가 들어올 수 있으므로 화면에서는 줄바꿈이 유지되도록
 * 표시합니다.
 * 
 * 추가 보완: - car_model_no 컬럼을 추가로 매핑합니다. - 이 값은 차량 모델 통계용 식별값입니다. - 예:
 * car_tbl.car_no = 1(EV4), 5(EV3), 9(EV6) ...
 */
@Entity
@Table(name = "consult_tbl")
public class DealerConsult {

	// =========================
	// 기본 식별 정보
	// =========================

	// 상담번호(PK)
	@Id
	@Column(name = "consult_no")
	private Integer consultNo;

	// 회원번호
	@Column(name = "member_no")
	private Integer memberNo;

	// 차량번호(번호판)
	@Column(name = "car_no")
	private String carNo;

	// 차량 모델 번호(car_tbl.car_no 와 연결되는 값)
	@Column(name = "car_model_no")
	private Integer carModelNo;

	// 딜러번호
	@Column(name = "dealer_no")
	private Integer dealerNo;

	// =========================
	// 상담 정보
	// =========================

	// 예산금액
	@Column(name = "budget_amount")
	private Integer budgetAmount;

	// 사용목적
	@Column(name = "use_purpose")
	private String usePurpose;

	// 주행거리 정보
	@Column(name = "main_range_km")
	private String mainRangeKm;

	// 동승자 / 가족 정보
	@Column(name = "fellow_data")
	private String fellowData;

	// 상담내용
	@Column(name = "consult_content")
	private String consultContent;

	// 상담상태
	// 예: WAITING / IN_PROGRESS / COMPLETED
	@Column(name = "consult_status")
	private String consultStatus;

	// 상담신청일시
	@Column(name = "request_date")
	private LocalDateTime requestDate;

	// 배정일시
	@Column(name = "assigned_date")
	private LocalDateTime assignedDate;

	// 완료일시
	@Column(name = "completed_date")
	private LocalDateTime completedDate;

	/**
	 * 상담 메모
	 * 
	 * 고객 / 관리자 측에서 작성한 공용 메모 컬럼입니다. 딜러는 이 값을 읽기 전용으로만 확인합니다.
	 */
	@Column(name = "consult_memo")
	private String consultMemo;

	/**
	 * 화면 표시용 상담 상태명 반환
	 * 
	 * DB 값이 영문코드 / 한글값 어느 쪽이 들어와도 화면에는 한글로 보여주도록 처리합니다.
	 * 
	 * 예: - WAITING / 대기 -> 대기 - IN_PROGRESS / 진행중 -> 진행중 - COMPLETED / 완료 -> 완료
	 */
	@Transient
	public String getConsultStatusKor() {

		// null 방어
		if (consultStatus == null) {
			return "-";
		}

		// 영문 코드 / 한글 모두 한글 표시로 통일
		if ("WAITING".equals(consultStatus) || "대기".equals(consultStatus)) {
			return "대기";
		} else if ("IN_PROGRESS".equals(consultStatus) || "진행중".equals(consultStatus)) {
			return "진행중";
		} else if ("COMPLETED".equals(consultStatus) || "완료".equals(consultStatus)) {
			return "완료";
		}

		// 예외적인 상태값은 원본 그대로 반환
		return consultStatus;
	}

	/**
	 * 화면 표시용 상담 메모 반환
	 * 
	 * consult_memo 값이 없으면 "-" 를 반환해서 HTML 쪽에서 null 처리를 단순화할 때 사용할 수 있습니다.
	 */
	@Transient
	public String getConsultMemoDisplay() {
		if (consultMemo == null || consultMemo.trim().isEmpty()) {
			return "-";
		}
		return consultMemo;
	}

	// =========================
	// getter / setter
	// =========================

	public Integer getConsultNo() {
		return consultNo;
	}

	public void setConsultNo(Integer consultNo) {
		this.consultNo = consultNo;
	}

	public Integer getMemberNo() {
		return memberNo;
	}

	public void setMemberNo(Integer memberNo) {
		this.memberNo = memberNo;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public Integer getCarModelNo() {
		return carModelNo;
	}

	public void setCarModelNo(Integer carModelNo) {
		this.carModelNo = carModelNo;
	}

	public Integer getDealerNo() {
		return dealerNo;
	}

	public void setDealerNo(Integer dealerNo) {
		this.dealerNo = dealerNo;
	}

	public Integer getBudgetAmount() {
		return budgetAmount;
	}

	public void setBudgetAmount(Integer budgetAmount) {
		this.budgetAmount = budgetAmount;
	}

	public String getUsePurpose() {
		return usePurpose;
	}

	public void setUsePurpose(String usePurpose) {
		this.usePurpose = usePurpose;
	}

	public String getMainRangeKm() {
		return mainRangeKm;
	}

	public void setMainRangeKm(String mainRangeKm) {
		this.mainRangeKm = mainRangeKm;
	}

	public String getFellowData() {
		return fellowData;
	}

	public void setFellowData(String fellowData) {
		this.fellowData = fellowData;
	}

	public String getConsultContent() {
		return consultContent;
	}

	public void setConsultContent(String consultContent) {
		this.consultContent = consultContent;
	}

	public String getConsultStatus() {
		return consultStatus;
	}

	public void setConsultStatus(String consultStatus) {
		this.consultStatus = consultStatus;
	}

	public LocalDateTime getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(LocalDateTime requestDate) {
		this.requestDate = requestDate;
	}

	public LocalDateTime getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(LocalDateTime assignedDate) {
		this.assignedDate = assignedDate;
	}

	public LocalDateTime getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(LocalDateTime completedDate) {
		this.completedDate = completedDate;
	}

	public String getConsultMemo() {
		return consultMemo;
	}

	public void setConsultMemo(String consultMemo) {
		this.consultMemo = consultMemo;
	}
}