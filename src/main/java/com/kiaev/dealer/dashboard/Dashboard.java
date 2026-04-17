package com.kiaev.dealer.dashboard;

/**
 * 딜러 대시보드 통계 데이터 DTO
 *
 * 기능 - 로그인 딜러 기본 정보 - 상담 건수 통계 - 판매 건수 / 판매 금액 통계
 */
public class Dashboard {

	// =========================
	// 딜러 기본 정보
	// =========================
	private Integer dealerNo;
	private String dealerName;

	// =========================
	// 상담 통계
	// =========================
	private Integer totalConsultCount;
	private Integer waitingConsultCount;
	private Integer inProgressConsultCount;
	private Integer completedConsultCount;

	// =========================
	// 판매 통계
	// =========================
	private Integer totalSalesCount;
	private Integer totalSalesAmount;

	public Integer getDealerNo() {
		return dealerNo;
	}

	public void setDealerNo(Integer dealerNo) {
		this.dealerNo = dealerNo;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public Integer getTotalConsultCount() {
		return totalConsultCount;
	}

	public void setTotalConsultCount(Integer totalConsultCount) {
		this.totalConsultCount = totalConsultCount;
	}

	public Integer getWaitingConsultCount() {
		return waitingConsultCount;
	}

	public void setWaitingConsultCount(Integer waitingConsultCount) {
		this.waitingConsultCount = waitingConsultCount;
	}

	public Integer getInProgressConsultCount() {
		return inProgressConsultCount;
	}

	public void setInProgressConsultCount(Integer inProgressConsultCount) {
		this.inProgressConsultCount = inProgressConsultCount;
	}

	public Integer getCompletedConsultCount() {
		return completedConsultCount;
	}

	public void setCompletedConsultCount(Integer completedConsultCount) {
		this.completedConsultCount = completedConsultCount;
	}

	public Integer getTotalSalesCount() {
		return totalSalesCount;
	}

	public void setTotalSalesCount(Integer totalSalesCount) {
		this.totalSalesCount = totalSalesCount;
	}

	public Integer getTotalSalesAmount() {
		return totalSalesAmount;
	}

	public void setTotalSalesAmount(Integer totalSalesAmount) {
		this.totalSalesAmount = totalSalesAmount;
	}
}