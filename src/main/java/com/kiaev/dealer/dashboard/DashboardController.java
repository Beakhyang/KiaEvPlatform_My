package com.kiaev.dealer.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kiaev.dealer.login.DealerLogin;

import jakarta.servlet.http.HttpSession;

/**
 * 딜러 대시보드 Controller
 *
 * 주요 기능 1. 로그인한 딜러만 접근 가능 2. 딜러 메인 페이지 제공 3. 기존 /dealer/dashboard 접근 시
 * /dealer/main 으로 리다이렉트 4. 통계 화면 제공
 *
 * 보완 사항 - dashboard null 방지 - 월별 / 차량별 통계 리스트 null 방지 - Chart.js 에서 사용할 labels
 * / counts / amounts 배열 분리 - 기존 기능 및 URL 구조는 절대 제거하지 않음
 */
@Controller
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	/**
	 * 딜러 메인 페이지
	 *
	 * 기존 dashboardMain.html 을 메인 화면으로 사용합니다.
	 *
	 * 처리 순서 1. 세션에서 로그인 딜러 조회 2. 로그인 안 되어 있으면 로그인 페이지로 이동 3. 딜러 요약 통계 조회 4. 화면에 전달
	 */
	@GetMapping("/dealer/main")
	public String dealerMain(HttpSession session, Model model) {

		// =========================
		// 1. 세션에서 로그인 딜러 정보 조회
		// =========================
		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		// =========================
		// 2. 로그인되지 않은 경우 로그인 페이지로 이동
		// =========================
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		// =========================
		// 3. 딜러 메인 화면용 요약 정보 조회
		// =========================
		Dashboard dashboard = dashboardService.getDashboardSummary(loginDealer.getDealerNo(),
				loginDealer.getDealerName());

		// =========================
		// 4. 화면에 필요한 데이터 전달
		// =========================
		model.addAttribute("dashboard", dashboard);
		model.addAttribute("loginDealer", loginDealer);

		// 기존 메인 화면 유지
		return "dealer/dashboard/dashboardMain";
	}

	/**
	 * 기존 /dealer/dashboard URL 접근 시 /dealer/main 으로 이동시켜 기존 링크와 호환합니다.
	 *
	 * 기존에 만들어둔 링크가 깨지지 않도록 유지하는 용도입니다.
	 */
	@GetMapping("/dealer/dashboard")
	public String dashboardRedirect(HttpSession session) {

		// 세션에서 로그인 딜러 조회
		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		// 로그인 안 된 경우 로그인 페이지로 이동
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		// 로그인 상태면 메인 페이지로 이동
		return "redirect:/dealer/main";
	}

	/**
	 * 통계 화면
	 *
	 * 제공 데이터 1. 요약 통계 2. 월별 판매 통계 3. 차량 종류별 판매 통계 4. Chart.js 에 사용할 배열 데이터
	 *
	 * 기존 기능은 유지하면서, 화면에서 바로 사용할 수 있도록 labels / counts / amounts 를 따로 분리하여 전달합니다.
	 */
	@GetMapping("/dealer/statistics")
	public String statisticsMain(HttpSession session, Model model) {

		// =========================
		// 1. 로그인 딜러 확인
		// =========================
		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		// 로그인 안 된 경우 로그인 페이지로 이동
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		String dealerName = loginDealer.getDealerName();

		// =========================
		// 2. 요약 통계 조회
		// =========================
		Dashboard dashboard = dashboardService.getDashboardSummary(dealerNo, dealerName);

		// =========================
		// 3. 월별 판매 통계 조회
		// =========================
		List<MonthlySalesStat> monthlySalesStats = dashboardService.getMonthlySalesStats(dealerNo);

		// null 방어
		if (monthlySalesStats == null) {
			monthlySalesStats = new ArrayList<>();
		}

		// Chart.js 에서 사용할 월별 차트 데이터 분리
		List<String> monthlyLabels = new ArrayList<>();
		List<Integer> monthlyCounts = new ArrayList<>();
		List<Integer> monthlyAmounts = new ArrayList<>();

		for (MonthlySalesStat stat : monthlySalesStats) {
			if (stat == null) {
				continue;
			}

			// 판매월
			String salesMonth = stat.getSalesMonth();
			if (salesMonth == null || salesMonth.trim().isEmpty()) {
				salesMonth = "";
			}

			// 판매 건수
			Integer salesCount = stat.getSalesCount();
			if (salesCount == null) {
				salesCount = 0;
			}

			// 판매 금액
			Integer salesAmount = stat.getSalesAmount();
			if (salesAmount == null) {
				salesAmount = 0;
			}

			monthlyLabels.add(salesMonth);
			monthlyCounts.add(salesCount);
			monthlyAmounts.add(salesAmount);
		}

		// =========================
		// 4. 차량 종류별 판매 통계 조회
		// =========================
		List<CarModelSalesStat> carModelSalesStats = dashboardService.getCarModelSalesStats(dealerNo);

		// null 방어
		if (carModelSalesStats == null) {
			carModelSalesStats = new ArrayList<>();
		}

		// Chart.js 에서 사용할 차량별 차트 데이터 분리
		List<String> carModelLabels = new ArrayList<>();
		List<Integer> carModelCounts = new ArrayList<>();
		List<Integer> carModelAmounts = new ArrayList<>();

		for (CarModelSalesStat stat : carModelSalesStats) {
			if (stat == null) {
				continue;
			}

			// 차량 모델명
			String modelName = stat.getModelName();
			if (modelName == null || modelName.trim().isEmpty()) {
				modelName = "미분류 차량";
			}

			// 판매 건수
			Integer salesCount = stat.getSalesCount();
			if (salesCount == null) {
				salesCount = 0;
			}

			// 판매 금액
			Integer salesAmount = stat.getSalesAmount();
			if (salesAmount == null) {
				salesAmount = 0;
			}

			carModelLabels.add(modelName);
			carModelCounts.add(salesCount);
			carModelAmounts.add(salesAmount);
		}

		// =========================
		// 5. 화면 전달
		// =========================
		model.addAttribute("dashboard", dashboard);
		model.addAttribute("loginDealer", loginDealer);

		// 테이블 출력용 원본 리스트
		model.addAttribute("monthlySalesStats", monthlySalesStats);
		model.addAttribute("carModelSalesStats", carModelSalesStats);

		// 월별 차트용 배열
		model.addAttribute("monthlyLabels", monthlyLabels);
		model.addAttribute("monthlyCounts", monthlyCounts);
		model.addAttribute("monthlyAmounts", monthlyAmounts);

		// 차량 종류별 차트용 배열
		model.addAttribute("carModelLabels", carModelLabels);
		model.addAttribute("carModelCounts", carModelCounts);
		model.addAttribute("carModelAmounts", carModelAmounts);

		// 기존 통계 화면 유지
		return "dealer/dashboard/statisticsMain";
	}
}