package com.kiaev.dealer.sales;

import java.util.List;

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

	boolean existsByConsultNo(Integer consultNo);

	long countByDealerNo(Integer dealerNo);

	@Query("SELECT COALESCE(SUM(s.salesAmount), 0) FROM Sales s WHERE s.dealerNo = :dealerNo")
	Long sumSalesAmountByDealerNo(@Param("dealerNo") Integer dealerNo);
}
