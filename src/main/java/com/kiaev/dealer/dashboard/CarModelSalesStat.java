package com.kiaev.dealer.dashboard;

/**
 * 차량 종류별 판매 통계 DTO
 *
 * 사용 목적 - 차량 모델명별 판매 건수 - 차량 모델명별 판매 금액 합계
 *
 * 예시 - EV3 : 3건 - EV6 : 5건 - EV9 : 2건
 */
public class CarModelSalesStat {

	// =========================
	// 차량 모델명
	// 예: EV3, EV6, EV9, 미분류 차량
	// =========================
	private String modelName;

	// =========================
	// 판매 건수
	// =========================
	private Integer salesCount;

	// =========================
	// 판매 금액 합계
	// =========================
	private Integer salesAmount;

	/**
	 * 차량 모델명 조회
	 *
	 * @return 차량 모델명
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * 차량 모델명 저장
	 *
	 * @param modelName 차량 모델명
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
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