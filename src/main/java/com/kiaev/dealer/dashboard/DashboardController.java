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
 * 기능
 * - 로그인한 딜러만 접근 가능
 * - 메인 대시보드 화면 제공
 * - 통계 화면 제공
 * - 메인/통계 화면에서 공통으로 사용하는 상담/판매 통계 데이터 구성
 */
@Controller
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	/**
	 * 딜러 메인 페이지
	 *
	 * 요구사항에 맞춰 메인 대시보드에서도 상담현황과 판매현황을 함께 확인할 수 있도록
	 * 요약 통계와 차트용 데이터를 같이 내려준다.
	 */
	@GetMapping("/dealer/main")
	public String dealerMain(HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		String dealerName = loginDealer.getDealerName();

		Dashboard dashboard = dashboardService.getDashboardSummary(dealerNo, dealerName);

		addDashboardStatisticsAttributes(model, dealerNo, dashboard);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/dashboard/dashboardMain";
	}

	/**
	 * 기존 /dealer/dashboard URL은 메인 대시보드로 유지한다.
	 */
	@GetMapping("/dealer/dashboard")
	public String dashboardRedirect(HttpSession session) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		return "redirect:/dealer/main";
	}

	/**
	 * 딜러 통계 페이지
	 */
	@GetMapping("/dealer/statistics")
	public String statisticsMain(HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		String dealerName = loginDealer.getDealerName();

		Dashboard dashboard = dashboardService.getDashboardSummary(dealerNo, dealerName);

		addDashboardStatisticsAttributes(model, dealerNo, dashboard);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/dashboard/statisticsMain";
	}

	/**
	 * 메인 대시보드와 통계 페이지가 함께 사용하는 공통 통계/차트 데이터 세팅
	 */
	private void addDashboardStatisticsAttributes(Model model, Integer dealerNo, Dashboard dashboard) {

		List<MonthlySalesStat> monthlySalesStats = dashboardService.getMonthlySalesStats(dealerNo);
		if (monthlySalesStats == null) {
			monthlySalesStats = new ArrayList<>();
		}

		List<String> monthlyLabels = new ArrayList<>();
		List<Integer> monthlyCounts = new ArrayList<>();
		List<Integer> monthlyAmounts = new ArrayList<>();

		for (MonthlySalesStat stat : monthlySalesStats) {
			if (stat == null) {
				continue;
			}

			String salesMonth = stat.getSalesMonth();
			if (salesMonth == null || salesMonth.trim().isEmpty()) {
				salesMonth = "";
			}

			Integer salesCount = stat.getSalesCount();
			if (salesCount == null) {
				salesCount = 0;
			}

			Integer salesAmount = stat.getSalesAmount();
			if (salesAmount == null) {
				salesAmount = 0;
			}

			monthlyLabels.add(salesMonth);
			monthlyCounts.add(salesCount);
			monthlyAmounts.add(salesAmount);
		}

		List<CarModelSalesStat> carModelSalesStats = dashboardService.getCarModelSalesStats(dealerNo);
		if (carModelSalesStats == null) {
			carModelSalesStats = new ArrayList<>();
		}

		List<String> carModelLabels = new ArrayList<>();
		List<Integer> carModelCounts = new ArrayList<>();
		List<Integer> carModelAmounts = new ArrayList<>();

		for (CarModelSalesStat stat : carModelSalesStats) {
			if (stat == null) {
				continue;
			}

			String modelName = stat.getModelName();
			if (modelName == null || modelName.trim().isEmpty()) {
				modelName = "미분류 차량";
			}

			Integer salesCount = stat.getSalesCount();
			if (salesCount == null) {
				salesCount = 0;
			}

			Integer salesAmount = stat.getSalesAmount();
			if (salesAmount == null) {
				salesAmount = 0;
			}

			carModelLabels.add(modelName);
			carModelCounts.add(salesCount);
			carModelAmounts.add(salesAmount);
		}

		model.addAttribute("dashboard", dashboard);
		model.addAttribute("monthlySalesStats", monthlySalesStats);
		model.addAttribute("carModelSalesStats", carModelSalesStats);
		model.addAttribute("monthlyLabels", monthlyLabels);
		model.addAttribute("monthlyCounts", monthlyCounts);
		model.addAttribute("monthlyAmounts", monthlyAmounts);
		model.addAttribute("carModelLabels", carModelLabels);
		model.addAttribute("carModelCounts", carModelCounts);
		model.addAttribute("carModelAmounts", carModelAmounts);
	}
}
