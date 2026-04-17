package com.kiaev.dealer.sales;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 판매 엔티티
 * 
 * sales_tbl 테이블과 매핑됩니다. 판매 등록 1건의 정보를 저장합니다.
 * 
 * 중요 수정 사항 - 현재 DealerConsult.carNo 가 String 이므로 Sales.carNo 도 String 으로 맞춤 -
 * 기존 기능은 절대 삭제하지 않고 유지 - 통계 / 목록 / 상세 / 저장 흐름은 그대로 유지 - created_at 은 DB
 * 기본값(CURRENT_TIMESTAMP)으로 자동 저장되도록 insert/update 제외 처리
 */
@Entity
@Table(name = "sales_tbl")
public class Sales {

	// =========================
	// 기본 식별 정보
	// =========================

	// 판매번호(PK)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sales_no")
	private Integer salesNo;

	/**
	 * 차량번호
	 * 
	 * 중요: - 현재 consult_tbl 의 DealerConsult.carNo 타입이 String 이므로 여기서도 String 으로 맞춰야
	 * SalesService 와 타입 충돌이 나지 않습니다.
	 */
	@Column(name = "car_no")
	private String carNo;

	// 차량 모델명
	@Column(name = "model_name")
	private String modelName;

	// 상담번호
	@Column(name = "consult_no")
	private Integer consultNo;

	// 회원번호
	@Column(name = "member_no")
	private Integer memberNo;

	// 딜러번호
	@Column(name = "dealer_no")
	private Integer dealerNo;

	// 판매금액
	@Column(name = "sales_amount")
	private Integer salesAmount;

	// 판매일시
	@Column(name = "sales_date")
	private LocalDateTime salesDate;

	// 생성일시
	// DB에서 CURRENT_TIMESTAMP 로 자동 저장되므로 insert / update 에서 제외
	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	// 판매상태
	@Column(name = "sales_status")
	private String salesStatus;

	// 차량 모델 번호
	@Column(name = "car_model_no")
	private Integer carModelNo;

	// =========================
	// getter / setter
	// =========================

	public Integer getSalesNo() {
		return salesNo;
	}

	public void setSalesNo(Integer salesNo) {
		this.salesNo = salesNo;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
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

	public Integer getCarModelNo() {
		return carModelNo;
	}

	public void setCarModelNo(Integer carModelNo) {
		this.carModelNo = carModelNo;
	}
}