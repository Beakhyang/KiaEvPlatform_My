package com.kiaev.dealer.dealerconsult;

import java.time.LocalDateTime;

/**
 * 딜러 상담 화면 전용 DTO
 *
 * 상담 목록/상세 화면에서 요구하는 고객 정보와 차량 모델명을 함께 담아
 * 템플릿에서 바로 사용할 수 있도록 만든 뷰 모델이다.
 */
public class DealerConsultView {

	private Integer consultNo;
	private Integer memberNo;
	private Integer dealerNo;
	private Integer budgetAmount;
	private String customerName;
	private String customerPhone;
	private String customerEmail;
	private String carModelName;
	private String consultContent;
	private String consultMemo;
	private String consultStatus;
	private String usePurpose;
	private String mainRangeKm;
	private String fellowData;
	private LocalDateTime requestDate;
	private LocalDateTime assignedDate;
	private LocalDateTime completedDate;

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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCarModelName() {
		return carModelName;
	}

	public void setCarModelName(String carModelName) {
		this.carModelName = carModelName;
	}

	public String getConsultContent() {
		return consultContent;
	}

	public void setConsultContent(String consultContent) {
		this.consultContent = consultContent;
	}

	public String getConsultMemo() {
		return consultMemo;
	}

	public void setConsultMemo(String consultMemo) {
		this.consultMemo = consultMemo;
	}

	public String getConsultStatus() {
		return consultStatus;
	}

	public void setConsultStatus(String consultStatus) {
		this.consultStatus = consultStatus;
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

	public String getCustomerNameDisplay() {
		return isBlank(customerName) ? "-" : customerName;
	}

	public String getCustomerPhoneDisplay() {
		return isBlank(customerPhone) ? "-" : customerPhone;
	}

	public String getCustomerEmailDisplay() {
		return isBlank(customerEmail) ? "-" : customerEmail;
	}

	public String getCarModelNameDisplay() {
		return isBlank(carModelName) ? "차량 정보 없음" : carModelName;
	}

	public String getConsultContentDisplay() {
		return isBlank(consultContent) ? "-" : consultContent;
	}

	public String getConsultMemoDisplay() {
		return isBlank(consultMemo) ? "-" : consultMemo;
	}

	public String getConsultStatusCode() {
		if (consultStatus == null) {
			return "";
		}
		return consultStatus.trim();
	}

	public String getConsultStatusLabel() {
		switch (getConsultStatusCode()) {
		case "WAITING":
			return "신규 상담신청";
		case "IN_PROGRESS":
			return "진행중";
		case "COMPLETED":
			return "완료";
		default:
			return isBlank(consultStatus) ? "-" : consultStatus;
		}
	}

	public boolean isWaitingStatus() {
		return "WAITING".equals(getConsultStatusCode());
	}

	public boolean isInProgressStatus() {
		return "IN_PROGRESS".equals(getConsultStatusCode());
	}

	public boolean isCompletedStatus() {
		return "COMPLETED".equals(getConsultStatusCode());
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
