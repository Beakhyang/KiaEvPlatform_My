package com.kiaev.dealer.sales;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kiaev.dealer.dealerconsult.DealerConsultView;
import com.kiaev.dealer.login.DealerLogin;

import jakarta.servlet.http.HttpSession;

@Controller
public class SalesController {

	@Autowired
	private SalesService salesService;

	@GetMapping("/dealer/sales/list")
	public String salesList(HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		List<SalesView> salesList = salesService.getSalesListByDealerNo(dealerNo);
		long salesCount = salesService.getSalesCountByDealerNo(dealerNo);
		Long totalSalesAmount = salesService.getSalesAmountSumByDealerNo(dealerNo);

		model.addAttribute("salesList", salesList);
		model.addAttribute("salesCount", salesCount);
		model.addAttribute("totalSalesAmount", totalSalesAmount);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/sales/salesList";
	}

	@GetMapping("/dealer/sales/detail")
	public String salesDetail(@RequestParam("salesNo") Integer salesNo, HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		SalesView sales = salesService.getSalesDetailByDealerNo(salesNo, loginDealer.getDealerNo());
		if (sales == null) {
			return "redirect:/dealer/sales/list";
		}

		model.addAttribute("sales", sales);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/sales/salesDetail";
	}

	@GetMapping("/dealer/sales/register")
	public String salesRegisterForm(@RequestParam(value = "consultNo", required = false) Integer consultNo,
			HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		populateSalesRegisterModel(model, loginDealer, consultNo, null, null);
		return "dealer/sales/salesRegister";
	}

	@GetMapping("/dealer/sales/register/select")
	public String selectConsultForSales(@RequestParam("consultNo") Integer consultNo, HttpSession session) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		return "redirect:/dealer/sales/register?consultNo=" + consultNo;
	}

	@PostMapping("/dealer/sales/register")
	public String salesRegister(@RequestParam("consultNo") Integer consultNo,
			@RequestParam("salesAmount") Integer salesAmount, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		String resultMessage = salesService.registerSales(consultNo, loginDealer.getDealerNo(), salesAmount);
		if (SalesService.REGISTER_SUCCESS_MESSAGE.equals(resultMessage)) {
			redirectAttributes.addFlashAttribute("successMessage", resultMessage);
			return "redirect:/dealer/sales/list";
		}

		populateSalesRegisterModel(model, loginDealer, consultNo, salesAmount, resultMessage);
		return "dealer/sales/salesRegister";
	}

	private void populateSalesRegisterModel(Model model, DealerLogin loginDealer, Integer consultNo, Integer salesAmount,
			String errorMessage) {

		Integer dealerNo = loginDealer.getDealerNo();
		List<DealerConsultView> completedConsultList = salesService.getCompletedConsultList(dealerNo);
		DealerConsultView selectedConsult = null;
		Sales sales = new Sales();

		if (consultNo != null) {
			selectedConsult = salesService.getCompletedConsultDetail(consultNo, dealerNo);

			Sales selectedSales = salesService.createSalesFromConsult(consultNo, dealerNo);
			if (selectedSales != null) {
				sales = selectedSales;
			} else {
				sales.setConsultNo(consultNo);
				if (errorMessage == null) {
					errorMessage = "선택한 상담으로 판매 등록을 진행할 수 없습니다.";
				}
			}
		}

		if (salesAmount != null) {
			sales.setSalesAmount(salesAmount);
		}

		model.addAttribute("completedConsultList", completedConsultList);
		model.addAttribute("selectedConsultNo", consultNo);
		model.addAttribute("selectedConsult", selectedConsult);
		model.addAttribute("sales", sales);
		model.addAttribute("loginDealer", loginDealer);

		if (errorMessage != null) {
			model.addAttribute("errorMessage", errorMessage);
		}
	}
}
