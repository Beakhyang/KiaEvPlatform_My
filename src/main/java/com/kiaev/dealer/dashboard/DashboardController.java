package com.kiaev.dealer.dashboard;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kiaev.dealer.login.DealerLogin;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

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

	@GetMapping("/dealer/dashboard")
	public String dashboardRedirect(HttpSession session) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		return "redirect:/dealer/main";
	}

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

			monthlyLabels.add(stat.getSalesMonth() != null ? stat.getSalesMonth() : "");
			monthlyCounts.add(stat.getSalesCount() != null ? stat.getSalesCount() : 0);
			monthlyAmounts.add(stat.getSalesAmount() != null ? stat.getSalesAmount() : 0);
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

			carModelLabels.add(modelName);
			carModelCounts.add(stat.getSalesCount() != null ? stat.getSalesCount() : 0);
			carModelAmounts.add(stat.getSalesAmount() != null ? stat.getSalesAmount() : 0);
		}

		int totalSalesCount = dashboard != null && dashboard.getTotalSalesCount() != null ? dashboard.getTotalSalesCount() : 0;
		int totalSalesAmount = dashboard != null && dashboard.getTotalSalesAmount() != null ? dashboard.getTotalSalesAmount() : 0;
		long averageSalesAmount = totalSalesCount > 0 ? Math.round((double) totalSalesAmount / totalSalesCount) : 0L;

		MonthlySalesStat currentMonthStat = findCurrentMonthStat(monthlySalesStats);
		int currentMonthSalesCount = currentMonthStat != null && currentMonthStat.getSalesCount() != null
				? currentMonthStat.getSalesCount()
				: 0;
		int currentMonthSalesAmount = currentMonthStat != null && currentMonthStat.getSalesAmount() != null
				? currentMonthStat.getSalesAmount()
				: 0;

		CarModelSalesStat topSellingModelStat = findTopSellingModelStat(carModelSalesStats);
		String topSellingModelName = topSellingModelStat != null && topSellingModelStat.getModelName() != null
				&& !topSellingModelStat.getModelName().trim().isEmpty() ? topSellingModelStat.getModelName() : "집계된 차량 없음";
		int topSellingModelCount = topSellingModelStat != null && topSellingModelStat.getSalesCount() != null
				? topSellingModelStat.getSalesCount()
				: 0;
		int topSellingModelAmount = topSellingModelStat != null && topSellingModelStat.getSalesAmount() != null
				? topSellingModelStat.getSalesAmount()
				: 0;

		model.addAttribute("dashboard", dashboard);
		model.addAttribute("monthlySalesStats", monthlySalesStats);
		model.addAttribute("carModelSalesStats", carModelSalesStats);
		model.addAttribute("monthlyLabels", monthlyLabels);
		model.addAttribute("monthlyCounts", monthlyCounts);
		model.addAttribute("monthlyAmounts", monthlyAmounts);
		model.addAttribute("carModelLabels", carModelLabels);
		model.addAttribute("carModelCounts", carModelCounts);
		model.addAttribute("carModelAmounts", carModelAmounts);
		model.addAttribute("averageSalesAmount", averageSalesAmount);
		model.addAttribute("currentMonthLabel", YearMonth.now().toString());
		model.addAttribute("currentMonthSalesCount", currentMonthSalesCount);
		model.addAttribute("currentMonthSalesAmount", currentMonthSalesAmount);
		model.addAttribute("topSellingModelName", topSellingModelName);
		model.addAttribute("topSellingModelCount", topSellingModelCount);
		model.addAttribute("topSellingModelAmount", topSellingModelAmount);
	}

	private MonthlySalesStat findCurrentMonthStat(List<MonthlySalesStat> monthlySalesStats) {

		if (monthlySalesStats == null || monthlySalesStats.isEmpty()) {
			return null;
		}

		String currentMonth = YearMonth.now().toString();

		for (MonthlySalesStat stat : monthlySalesStats) {
			if (stat == null || stat.getSalesMonth() == null) {
				continue;
			}

			if (currentMonth.equals(stat.getSalesMonth().trim())) {
				return stat;
			}
		}

		return null;
	}

	private CarModelSalesStat findTopSellingModelStat(List<CarModelSalesStat> carModelSalesStats) {

		if (carModelSalesStats == null || carModelSalesStats.isEmpty()) {
			return null;
		}

		return carModelSalesStats.get(0);
	}
}
