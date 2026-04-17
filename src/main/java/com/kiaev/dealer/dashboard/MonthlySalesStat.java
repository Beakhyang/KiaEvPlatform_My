package com.kiaev.dealer.dashboard;

/**
 * 월별 판매 통계 DTO
 *
 * 사용 목적 - 월별 판매 건수 - 월별 판매 금액 합계
 *
 * 예시 - 2026-04 : 판매 3건 / 판매금액 합계 - 2026-05 : 판매 5건 / 판매금액 합계
 */
public class MonthlySalesStat {

	// =========================
	// 판매 월
	// 예: 2026-04
	// =========================
	private String salesMonth;

	// =========================
	// 판매 건수
	// =========================
	private Integer salesCount;

	// =========================
	// 판매 금액 합계
	// =========================
	private Integer salesAmount;

	/**
	 * 판매 월 조회
	 *
	 * @return 판매 월
	 */
	public String getSalesMonth() {
		return salesMonth;
	}

	/**
	 * 판매 월 저장
	 *
	 * @param salesMonth 판매 월
	 */
	public void setSalesMonth(String salesMonth) {
		this.salesMonth = salesMonth;
	}

	/**
	 * 판매 건수 조회
	 *
	 * @return 판매 건수
	 */
	public Integer getSalesCount() {
		return salesCount;
	}

	/**
	 * 판매 건수 저장
	 *
	 * @param salesCount 판매 건수
	 */
	public void setSalesCount(Integer salesCount) {
		this.salesCount = salesCount;
	}

	/**
	 * 판매 금액 합계 조회
	 *
	 * @return 판매 금액 합계
	 */
	public Integer getSalesAmount() {
		return salesAmount;
	}

	/**
	 * 판매 금액 합계 저장
	 *
	 * @param salesAmount 판매 금액 합계
	 */
	public void setSalesAmount(Integer salesAmount) {
		this.salesAmount = salesAmount;
	}
}