package com.kiaev.dealer.dealerconsult;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 상담 Repository
 * 
 * 상담 데이터를 조회 / 저장합니다.
 */
@Repository
public interface DealerConsultRepository extends JpaRepository<DealerConsult, Integer> {

	/**
	 * 로그인한 딜러의 상담 목록 조회
	 * 
	 * 상담번호 내림차순 정렬
	 */
	List<DealerConsult> findByDealerNoOrderByConsultNoDesc(Integer dealerNo);

	/**
	 * 로그인한 딜러의 상담 목록 조회
	 * 
	 * 상담번호 오름차순 정렬 판매 등록 화면에서 순차 선택용
	 */
	List<DealerConsult> findByDealerNoOrderByConsultNoAsc(Integer dealerNo);

	/**
	 * 로그인한 딜러의 완료 상담 목록 조회
	 * 
	 * 상태값이 한글/영문 혼용될 수 있으므로 COMPLETED, 완료 둘 다 조회할 수 있도록 In 조건 사용
	 */
	List<DealerConsult> findByDealerNoAndConsultStatusInOrderByConsultNoAsc(Integer dealerNo,
			List<String> consultStatusList);

	/**
	 * 상담번호 + 딜러번호가 일치하는 상담 1건 조회
	 */
	Optional<DealerConsult> findByConsultNoAndDealerNo(Integer consultNo, Integer dealerNo);

	/**
	 * 대시보드용 전체 상담 수 조회
	 */
	long countByDealerNo(Integer dealerNo);

	/**
	 * 대시보드용 상태별 상담 수 조회
	 * 
	 * 한글 / 영문 상태가 섞여 있어도 각각 카운트할 수 있게 사용
	 */
	long countByDealerNoAndConsultStatus(Integer dealerNo, String consultStatus);
}