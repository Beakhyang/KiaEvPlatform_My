package com.kiaev.dealer.dealerconsult;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 딜러 상담 Repository
 *
 * 엔티티 조회는 상태 변경/판매 등록 연동에 사용하고,
 * 네이티브 조인 조회는 화면에 필요한 고객/차량 정보를 함께 가져오는 데 사용한다.
 */
@Repository
public interface DealerConsultRepository extends JpaRepository<DealerConsult, Integer> {

	@Query(value = """
			SELECT
			    c.consult_no,
			    c.member_no,
			    c.dealer_no,
			    c.budget_amount,
			    m.member_name,
			    m.phone,
			    m.email,
			    car.model_name,
			    c.consult_content,
			    c.consult_memo,
			    c.consult_status,
			    c.use_purpose,
			    c.main_range_km,
			    c.fellow_data,
			    c.request_date,
			    c.assigned_date,
			    c.completed_date
			FROM consult_tbl c
			INNER JOIN member_tbl m
			    ON c.member_no = m.member_no
			LEFT JOIN car_tbl car
			    ON c.car_no = car.car_no
			WHERE c.dealer_no = :dealerNo
			ORDER BY c.consult_no DESC
			""", nativeQuery = true)
	List<Object[]> findConsultListRowsByDealerNo(@Param("dealerNo") Integer dealerNo);

	@Query(value = """
			SELECT
			    c.consult_no,
			    c.member_no,
			    c.dealer_no,
			    c.budget_amount,
			    m.member_name,
			    m.phone,
			    m.email,
			    car.model_name,
			    c.consult_content,
			    c.consult_memo,
			    c.consult_status,
			    c.use_purpose,
			    c.main_range_km,
			    c.fellow_data,
			    c.request_date,
			    c.assigned_date,
			    c.completed_date
			FROM consult_tbl c
			INNER JOIN member_tbl m
			    ON c.member_no = m.member_no
			LEFT JOIN car_tbl car
			    ON c.car_no = car.car_no
			WHERE c.consult_no = :consultNo
			  AND c.dealer_no = :dealerNo
			""", nativeQuery = true)
	List<Object[]> findConsultDetailRowsByConsultNoAndDealerNo(@Param("consultNo") Integer consultNo,
			@Param("dealerNo") Integer dealerNo);

	/**
	 * 판매 등록에서 사용하는 기본 상담 엔티티 조회
	 */
	List<DealerConsult> findByDealerNoOrderByConsultNoAsc(Integer dealerNo);

	/**
	 * 판매 등록에서 사용하는 완료 상담 조회
	 */
	List<DealerConsult> findByDealerNoAndConsultStatusInOrderByConsultNoAsc(Integer dealerNo,
			List<String> consultStatusList);

	/**
	 * 상태 변경/권한 확인용 상담 엔티티 조회
	 */
	Optional<DealerConsult> findByConsultNoAndDealerNo(Integer consultNo, Integer dealerNo);

	long countByDealerNo(Integer dealerNo);

	long countByDealerNoAndConsultStatus(Integer dealerNo, String consultStatus);
}
