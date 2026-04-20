package com.kiaev.dealer.dealerconsult;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
			    COALESCE(
			        NULLIF(TRIM(car_model.model_name), ''),
			        NULLIF(TRIM(car_no_model.model_name), ''),
			        '차량 정보 없음'
			    ) AS model_name,
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
			LEFT JOIN car_tbl car_model
			    ON c.car_model_no = car_model.car_no
			LEFT JOIN car_tbl car_no_model
			    ON c.car_no IS NOT NULL
			   AND c.car_no REGEXP '^[0-9]+$'
			   AND CAST(c.car_no AS UNSIGNED) = car_no_model.car_no
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
			    COALESCE(
			        NULLIF(TRIM(car_model.model_name), ''),
			        NULLIF(TRIM(car_no_model.model_name), ''),
			        '차량 정보 없음'
			    ) AS model_name,
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
			LEFT JOIN car_tbl car_model
			    ON c.car_model_no = car_model.car_no
			LEFT JOIN car_tbl car_no_model
			    ON c.car_no IS NOT NULL
			   AND c.car_no REGEXP '^[0-9]+$'
			   AND CAST(c.car_no AS UNSIGNED) = car_no_model.car_no
			WHERE c.consult_no = :consultNo
			  AND c.dealer_no = :dealerNo
			""", nativeQuery = true)
	List<Object[]> findConsultDetailRowsByConsultNoAndDealerNo(@Param("consultNo") Integer consultNo,
			@Param("dealerNo") Integer dealerNo);

	List<DealerConsult> findByDealerNoOrderByConsultNoAsc(Integer dealerNo);

	List<DealerConsult> findByDealerNoAndConsultStatusInOrderByConsultNoAsc(Integer dealerNo,
			List<String> consultStatusList);

	Optional<DealerConsult> findByConsultNoAndDealerNo(Integer consultNo, Integer dealerNo);

	long countByDealerNo(Integer dealerNo);

	long countByDealerNoAndConsultStatus(Integer dealerNo, String consultStatus);
}
