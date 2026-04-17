package com.kiaev.dealer.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * 딜러 통계 Repository
 *
 * 역할 - 로그인한 딜러 기준 요약 통계 조회 - 월별 판매 통계 조회 - 차량 종류별 판매 통계 조회
 *
 * 주의 - 기존 기능은 절대로 삭제하지 않고 유지 - 조회 전용 Repository - Native Query 사용 - null 안전 처리
 * 포함
 *
 * 보완 사항 1. consult_status 에 '배정완료' 값이 존재하는 경우 진행중으로 통계 반영 2. 차량 종류별 판매 통계는
 * sales_tbl.model_name 값을 우선 사용 3. model_name 이 비어 있으면 car_model_no 또는 car_no 를
 * 통해 car_tbl.model_name 으로 보완 4. 기존 기능은 삭제하지 않고, 조회 정확도만 보완
 */
@Repository
public class DashboardRepository {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 딜러 메인 / 통계 화면 공통 요약 정보 조회
	 *
	 * @param dealerNo   딜러 번호
	 * @param dealerName 딜러명
	 * @return Dashboard 객체
	 */
	public Dashboard getDashboardSummary(Integer dealerNo, String dealerName) {

		Dashboard dashboard = new Dashboard();

		// =========================
		// 기본 딜러 정보 세팅
		// =========================
		dashboard.setDealerNo(dealerNo);
		dashboard.setDealerName(dealerName);

		// =========================
		// 상담 통계 조회
		// - 상태값이 영문 / 한글 혼용될 수 있음
		// - 실제 DB에 '배정완료' 값도 있으므로 진행중으로 포함
		// - 공백 방지를 위해 TRIM 처리
		// =========================
		String consultSql = """
				SELECT
				    COUNT(*) AS total_consult_count,
				    SUM(CASE
				            WHEN TRIM(consult_status) IN ('WAITING', '대기') THEN 1
				            ELSE 0
				        END) AS waiting_count,
				    SUM(CASE
				            WHEN TRIM(consult_status) IN ('IN_PROGRESS', '진행중', '배정완료') THEN 1
				            ELSE 0
				        END) AS in_progress_count,
				    SUM(CASE
				            WHEN TRIM(consult_status) IN ('COMPLETED', '완료') THEN 1
				            ELSE 0
				        END) AS completed_count
				FROM consult_tbl
				WHERE dealer_no = :dealerNo
				""";

		Object[] consultRow = (Object[]) entityManager.createNativeQuery(consultSql).setParameter("dealerNo", dealerNo)
				.getSingleResult();

		dashboard.setTotalConsultCount(toInteger(consultRow[0]));
		dashboard.setWaitingConsultCount(toInteger(consultRow[1]));
		dashboard.setInProgressConsultCount(toInteger(consultRow[2]));
		dashboard.setCompletedConsultCount(toInteger(consultRow[3]));

		// =========================
		// 판매 통계 조회
		// =========================
		String salesSql = """
				SELECT
				    COUNT(*) AS total_sales_count,
				    COALESCE(SUM(sales_amount), 0) AS total_sales_amount
				FROM sales_tbl
				WHERE dealer_no = :dealerNo
				""";

		Object[] salesRow = (Object[]) entityManager.createNativeQuery(salesSql).setParameter("dealerNo", dealerNo)
				.getSingleResult();

		dashboard.setTotalSalesCount(toInteger(salesRow[0]));
		dashboard.setTotalSalesAmount(toInteger(salesRow[1]));

		return dashboard;
	}

	/**
	 * 월별 판매 통계 조회
	 *
	 * @param dealerNo 딜러 번호
	 * @return 월별 판매 통계 목록
	 */
	public List<MonthlySalesStat> getMonthlySalesStats(Integer dealerNo) {

		String sql = """
				SELECT
				    DATE_FORMAT(s.sales_date, '%Y-%m') AS sales_month,
				    COUNT(*) AS sales_count,
				    COALESCE(SUM(s.sales_amount), 0) AS sales_amount
				FROM sales_tbl s
				WHERE s.dealer_no = :dealerNo
				GROUP BY DATE_FORMAT(s.sales_date, '%Y-%m')
				ORDER BY sales_month ASC
				""";

		List<?> results = entityManager.createNativeQuery(sql).setParameter("dealerNo", dealerNo).getResultList();

		List<MonthlySalesStat> list = new ArrayList<>();

		for (Object rowObj : results) {
			Object[] row = (Object[]) rowObj;

			MonthlySalesStat stat = new MonthlySalesStat();

			stat.setSalesMonth(row[0] != null ? row[0].toString() : "");
			stat.setSalesCount(toInteger(row[1]));
			stat.setSalesAmount(toInteger(row[2]));

			list.add(stat);
		}

		return list;
	}

	/**
	 * 차량 종류별 판매 통계 조회
	 *
	 * 현재 실제 sales_tbl 데이터를 보면 model_name 컬럼이 직접 존재합니다. 다만 저장 시 model_name 이 비어 있는
	 * 경우가 있을 수 있으므로, 아래 순서로 차량명을 보완합니다.
	 *
	 * 우선순위 1. sales_tbl.model_name 2. sales_tbl.car_model_no = car_tbl.car_no 로 조인한
	 * car_tbl.model_name 3. sales_tbl.car_no 가 숫자 문자열일 경우 car_tbl.car_no 와 비교한
	 * car_tbl.model_name 4. 모두 없으면 '미분류 차량'
	 *
	 * 중요 - 기존 기능은 절대로 삭제하지 않음 - 기존 model_name 우선 집계 방식은 그대로 유지 - 단, model_name 이 비어
	 * 있을 때만 보완 조회를 추가
	 *
	 * @param dealerNo 딜러 번호
	 * @return 차량 종류별 판매 통계 목록
	 */
	public List<CarModelSalesStat> getCarModelSalesStats(Integer dealerNo) {

		String sql = """
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
				WHERE s.dealer_no = :dealerNo
				GROUP BY
				    COALESCE(
				        NULLIF(TRIM(s.model_name), ''),
				        NULLIF(TRIM(c1.model_name), ''),
				        NULLIF(TRIM(c2.model_name), ''),
				        '미분류 차량'
				    )
				ORDER BY sales_count DESC, resolved_model_name ASC
				""";

		List<?> results = entityManager.createNativeQuery(sql).setParameter("dealerNo", dealerNo).getResultList();

		List<CarModelSalesStat> list = new ArrayList<>();

		for (Object rowObj : results) {
			Object[] row = (Object[]) rowObj;

			CarModelSalesStat stat = new CarModelSalesStat();

			String modelName = "미분류 차량";
			if (row[0] != null && !row[0].toString().trim().isEmpty()) {
				modelName = row[0].toString().trim();
			}
			stat.setModelName(modelName);

			stat.setSalesCount(toInteger(row[1]));
			stat.setSalesAmount(toInteger(row[2]));

			list.add(stat);
		}

		return list;
	}

	/**
	 * Object -> Integer 안전 변환
	 *
	 * @param value DB 결과값
	 * @return Integer 값, null 이거나 변환 실패 시 0
	 */
	private Integer toInteger(Object value) {
		if (value == null) {
			return 0;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		try {
			return Integer.parseInt(value.toString());
		} catch (Exception e) {
			return 0;
		}
	}
}