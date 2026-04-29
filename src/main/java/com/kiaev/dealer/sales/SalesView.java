package com.kiaev.dealer.sales;

import java.time.LocalDateTime;

/**
 * View model for dealer sales screens.
 */
public class SalesView {

	private Integer salesNo;
	private Integer consultNo;
	private Integer memberNo;
	private Integer dealerNo;
	private String customerName;
	private String customerPhone;
	private String customerEmail;
	private String carIdentifier;
	private String carModelName;
	private Integer salesAmount;
	private LocalDateTime salesDate;
	private LocalDateTime createdAt;
	private String salesStatus;
	private LocalDateTime consultRequestDate;
	private LocalDateTime consultCompletedDate;

	public Integer getSalesNo() {
		return salesNo;
	}

	public void setSalesNo(Integer salesNo) {
		this.salesNo = salesNo;
	}

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

	public String getCarIdentifier() {
		return carIdentifier;
	}

	public void setCarIdentifier(String carIdentifier) {
		this.carIdentifier = carIdentifier;
	}

	public String getCarModelName() {
		return carModelName;
	}

	public void setCarModelName(String carModelName) {
		this.carModelName = carModelName;
	}

	public Integer getSalesAmount() {
		return salesAmount;
	}

	public void setSalesAmount(Integer salesAmount) {
		this.salesAmount = salesAmount;
	}

	public LocalDateTime getSalesDate() {
		return salesDate;
	}

	public void setSalesDate(LocalDateTime salesDate) {
		this.salesDate = salesDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getSalesStatus() {
		return salesStatus;
	}

	public void setSalesStatus(String salesStatus) {
		this.salesStatus = salesStatus;
	}

	public LocalDateTime getConsultRequestDate() {
		return consultRequestDate;
	}

	public void setConsultRequestDate(LocalDateTime consultRequestDate) {
		this.consultRequestDate = consultRequestDate;
	}

	public LocalDateTime getConsultCompletedDate() {
		return consultCompletedDate;
	}

	public void setConsultCompletedDate(LocalDateTime consultCompletedDate) {
		this.consultCompletedDate = consultCompletedDate;
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

	public String getCarIdentifierDisplay() {
		return isBlank(carIdentifier) ? "-" : carIdentifier;
	}

	public String getCarModelNameDisplay() {
		return isBlank(carModelName) ? "차량 정보 없음" : carModelName;
	}

	public String getSalesStatusCode() {
		if (salesStatus == null) {
			return "";
		}

		String trimmedStatus = salesStatus.trim();

		switch (trimmedStatus) {
		case "WAITING":
		case "대기":
			return "WAITING";
		case "IN_PROGRESS":
		case "진행중":
			return "IN_PROGRESS";
		case "COMPLETED":
		case "완료":
		case "판매완료":
			return "COMPLETED";
		default:
			return trimmedStatus;
		}
	}

	public String getSalesStatusLabel() {
		switch (getSalesStatusCode()) {
		case "WAITING":
			return "등록 대기";
		case "IN_PROGRESS":
			return "진행중";
		case "COMPLETED":
			return "판매 완료";
		default:
			return isBlank(salesStatus) ? "-" : salesStatus;
		}
	}

	public boolean isWaitingStatus() {
		return "WAITING".equals(getSalesStatusCode());
	}

	public boolean isInProgressStatus() {
		return "IN_PROGRESS".equals(getSalesStatusCode());
	}

	public boolean isCompletedStatus() {
		return "COMPLETED".equals(getSalesStatusCode());
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
