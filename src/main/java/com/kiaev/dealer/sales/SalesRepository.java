package com.kiaev.dealer.sales;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 판매 Repository
 * 
 * 기능 - 딜러별 판매 목록 조회 - 딜러별 판매 상세 조회 - 상담번호 중복 판매 확인 - 차량번호 중복 판매 확인 - 딜러 기준 판매
 * 건수 조회 - 딜러 기준 판매 총액 조회
 * 
 * 중요 수정 사항 - DealerConsult.carNo / Sales.carNo 와 타입을 맞추기 위해 existsByCarNo 파라미터를
 * String 으로 수정 - 기존 기능은 그대로 유지
 */
public interface SalesRepository extends JpaRepository<Sales, Integer> {

	/**
	 * 로그인한 딜러 기준 판매 목록 조회
	 * 
	 * 판매번호 내림차순 정렬
	 */
	List<Sales> findByDealerNoOrderBySalesNoDesc(Integer dealerNo);

	/**
	 * 로그인한 딜러 본인 판매 상세 조회
	 */
	Optional<Sales> findBySalesNoAndDealerNo(Integer salesNo, Integer dealerNo);

	/**
	 * 이미 판매 등록된 상담인지 확인
	 */
	boolean existsByConsultNo(Integer consultNo);

	/**
	 * 이미 판매된 차량인지 확인
	 * 
	 * 중요: - 현재 carNo 는 String 타입으로 맞춰야 SalesService 와 충돌이 나지 않습니다.
	 */
	boolean existsByCarNo(String carNo);

	/**
	 * 딜러 기준 판매 건수 조회
	 */
	long countByDealerNo(Integer dealerNo);

	/**
	 * 딜러 기준 판매 총액 조회
	 */
	@Query("SELECT COALESCE(SUM(s.salesAmount), 0) FROM Sales s WHERE s.dealerNo = :dealerNo")
	Long sumSalesAmountByDealerNo(@Param("dealerNo") Integer dealerNo);
}