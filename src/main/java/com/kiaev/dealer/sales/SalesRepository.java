package com.kiaev.dealer.sales;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesRepository extends JpaRepository<Sales, Integer> {

	@Query(value = """
			SELECT
			    s.sales_no,
			    s.consult_no,
			    s.member_no,
			    s.dealer_no,
			    m.member_name,
			    m.phone,
			    m.email,
			    s.car_no,
			    COALESCE(
			        NULLIF(TRIM(s.model_name), ''),
			        NULLIF(TRIM(sales_car.model_name), ''),
			        NULLIF(TRIM(consult_car.model_name), ''),
			        '차량 정보 없음'
			    ) AS resolved_model_name,
			    s.sales_amount,
			    s.sales_date,
			    s.created_at,
			    s.sales_status,
			    c.request_date,
			    c.completed_date
			FROM sales_tbl s
			LEFT JOIN member_tbl m
			    ON s.member_no = m.member_no
			LEFT JOIN consult_tbl c
			    ON s.consult_no = c.consult_no
			LEFT JOIN car_tbl sales_car
			    ON s.car_model_no = sales_car.car_no
			LEFT JOIN car_tbl consult_car
			    ON c.car_model_no = consult_car.car_no
			WHERE s.dealer_no = :dealerNo
			ORDER BY s.sales_no DESC
			""", nativeQuery = true)
	List<Object[]> findSalesListRowsByDealerNo(@Param("dealerNo") Integer dealerNo);

	@Query(value = """
			SELECT
			    s.sales_no,
			    s.consult_no,
			    s.member_no,
			    s.dealer_no,
			    m.member_name,
			    m.phone,
			    m.email,
			    s.car_no,
			    COALESCE(
			        NULLIF(TRIM(s.model_name), ''),
			        NULLIF(TRIM(sales_car.model_name), ''),
			        NULLIF(TRIM(consult_car.model_name), ''),
			        '차량 정보 없음'
			    ) AS resolved_model_name,
			    s.sales_amount,
			    s.sales_date,
			    s.created_at,
			    s.sales_status,
			    c.request_date,
			    c.completed_date
			FROM sales_tbl s
			LEFT JOIN member_tbl m
			    ON s.member_no = m.member_no
			LEFT JOIN consult_tbl c
			    ON s.consult_no = c.consult_no
			LEFT JOIN car_tbl sales_car
			    ON s.car_model_no = sales_car.car_no
			LEFT JOIN car_tbl consult_car
			    ON c.car_model_no = consult_car.car_no
			WHERE s.sales_no = :salesNo
			  AND s.dealer_no = :dealerNo
			""", nativeQuery = true)
	List<Object[]> findSalesDetailRowsBySalesNoAndDealerNo(@Param("salesNo") Integer salesNo,
			@Param("dealerNo") Integer dealerNo);

	Optional<Sales> findBySalesNoAndDealerNo(Integer salesNo, Integer dealerNo);

	boolean existsByConsultNo(Integer consultNo);

	long countByDealerNo(Integer dealerNo);

	@Query("SELECT COALESCE(SUM(s.salesAmount), 0) FROM Sales s WHERE s.dealerNo = :dealerNo")
	Long sumSalesAmountByDealerNo(@Param("dealerNo") Integer dealerNo);

	@Query(value = """
			SELECT
			    COALESCE(
			        NULLIF(TRIM(s.model_name), ''),
			        NULLIF(TRIM(c1.model_name), ''),
			        NULLIF(TRIM(c2.model_name), ''),
			        '미분류 차량'
			    ) AS resolved_model_name,
			    COUNT(*) AS sales_count,
			    COALESCE(SUM(s.sales_amount), 0) AS sales_amount
			FROM sales_tbl s
			LEFT JOIN car_tbl c1
			    ON s.car_model_no = c1.car_no
			LEFT JOIN car_tbl c2
			    ON s.car_no IS NOT NULL
			   AND s.car_no REGEXP '^[0-9]+$'
			   AND CAST(s.car_no AS UNSIGNED) = c2.car_no
			WHERE TRIM(COALESCE(s.sales_status, '')) IN ('COMPLETED', '완료')
			GROUP BY
			    COALESCE(
			        NULLIF(TRIM(s.model_name), ''),
			        NULLIF(TRIM(c1.model_name), ''),
			        NULLIF(TRIM(c2.model_name), ''),
			        '미분류 차량'
			    )
			ORDER BY sales_count DESC, sales_amount DESC, resolved_model_name ASC
			""", nativeQuery = true)
	List<Object[]> findTopSellingModelStats(Pageable pageable);
}
