package com.kiaev.dealer.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 딜러 대시보드 Service
 *
 * 주요 기능 1. 딜러 번호 기준 대시보드 요약 통계 조회 2. 월별 판매 통계 조회 3. 차량 종류별 판매 통계 조회
 *
 * 보완 사항 - dashboard null 방지 - 월별 통계 null 방지 - 차량 종류별 통계 null 방지 - 기존 기능은 절대
 * 제거하지 않음
 */
@Service
public class DashboardService {

	@Autowired
	private DashboardRepository dashboardRepository;

	/**
	 * 딜러 메인 / 통계 화면 공통 요약 정보 조회
	 *
	 * repository 결과가 null 이더라도 화면이 깨지지 않도록 기본값이 채워진 Dashboard 객체를 반환합니다.
	 *
	 * @param dealerNo   딜러번호
	 * @param dealerName 딜러명
	 * @return null 방지된 Dashboard 객체
	 */
	public Dashboard getDashboardSummary(Integer dealerNo, String dealerName) {

		Dashboard dashboard = dashboardRepository.getDashboardSummary(dealerNo, dealerName);

		// =========================
		// 조회 결과가 없으면 기본 Dashboard 생성
		// =========================
		if (dashboard == null) {
			dashboard = createEmptyDashboard(dealerNo, dealerName);
		}

		// =========================
		// null 방어 처리
		// =========================
		if (dashboard.getDealerNo() == null) {
			dashboard.setDealerNo(dealerNo);
		}

		if (dashboard.getDealerName() == null || dashboard.getDealerName().trim().isEmpty()) {
			dashboard.setDealerName(dealerName);
		}

		if (dashboard.getTotalConsultCount() == null) {
			dashboard.setTotalConsultCount(0);
		}

		if (dashboard.getTotalSalesCount() == null) {
			dashboard.setTotalSalesCount(0);
		}

		if (dashboard.getTotalSalesAmount() == null) {
			dashboard.setTotalSalesAmount(0);
		}

		if (dashboard.getWaitingConsultCount() == null) {
			dashboard.setWaitingConsultCount(0);
		}

		if (dashboard.getInProgressConsultCount() == null) {
			dashboard.setInProgressConsultCount(0);
		}

		if (dashboard.getCompletedConsultCount() == null) {
			dashboard.setCompletedConsultCount(0);
		}

		return dashboard;
	}

	/**
	 * 월별 판매 통계 조회
	 *
	 * @param dealerNo 딜러번호
	 * @return null 방지된 월별 판매 통계 목록
	 */
	public List<MonthlySalesStat> getMonthlySalesStats(Integer dealerNo) {

		List<MonthlySalesStat> stats = dashboardRepository.getMonthlySalesStats(dealerNo);

		if (stats == null) {
			return new ArrayList<>();
		}

		return stats;
	}

	/**
	 * 차량 종류별 판매 통계 조회
	 *
	 * @param dealerNo 딜러번호
	 * @return null 방지된 차량 종류별 판매 통계 목록
	 */
	public List<CarModelSalesStat> getCarModelSalesStats(Integer dealerNo) {

		List<CarModelSalesStat> stats = dashboardRepository.getCarModelSalesStats(dealerNo);

		if (stats == null) {
			return new ArrayList<>();
		}

		return stats;
	}

	/**
	 * 기본 Dashboard 객체 생성
	 *
	 * 데이터가 하나도 없어도 화면에 0 값으로 표시되도록 기본값을 세팅합니다.
	 *
	 * @param dealerNo   딜러번호
	 * @param dealerName 딜러명
	 * @return 기본 Dashboard 객체
	 */
	private Dashboard createEmptyDashboard(Integer dealerNo, String dealerName) {

		Dashboard dashboard = new Dashboard();

		dashboard.setDealerNo(dealerNo);
		dashboard.setDealerName(dealerName);
		dashboard.setTotalConsultCount(0);
		dashboard.setTotalSalesCount(0);
		dashboard.setTotalSalesAmount(0);
		dashboard.setWaitingConsultCount(0);
		dashboard.setInProgressConsultCount(0);
		dashboard.setCompletedConsultCount(0);

		return dashboard;
	}
}