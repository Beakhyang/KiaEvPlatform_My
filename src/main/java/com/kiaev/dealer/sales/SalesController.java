package com.kiaev.dealer.sales;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kiaev.dealer.dealerconsult.DealerConsult;
import com.kiaev.dealer.login.DealerLogin;

import jakarta.servlet.http.HttpSession;

/**
 * 판매 관리 Controller
 * 
 * 기능 - 판매 목록 조회 - 판매 상세 조회 - 판매 등록 화면 조회 - 완료/진행중 상담 선택 - 판매 등록 처리
 * 
 * 기존 기능 유지
 */
@Controller
public class SalesController {

	@Autowired
	private SalesService salesService;

	/**
	 * 판매 목록 조회
	 * 
	 * URL: GET /dealer/sales/list
	 */
	@GetMapping("/dealer/sales/list")
	public String salesList(HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		List<Sales> salesList = salesService.getSalesListByDealerNo(dealerNo);

		model.addAttribute("salesList", salesList);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/sales/salesList";
	}

	/**
	 * 판매 상세 조회
	 * 
	 * URL: GET /dealer/sales/detail?salesNo=1
	 */
	@GetMapping("/dealer/sales/detail")
	public String salesDetail(@RequestParam("salesNo") Integer salesNo, HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		Sales sales = salesService.getSalesDetailByDealerNo(salesNo, dealerNo);

		if (sales == null) {
			return "redirect:/dealer/sales/list";
		}

		model.addAttribute("sales", sales);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/sales/salesDetail";
	}

	/**
	 * 판매 등록 화면 조회
	 * 
	 * URL: GET /dealer/sales/register URL: GET /dealer/sales/register?consultNo=1
	 */
	@GetMapping("/dealer/sales/register")
	public String salesRegisterForm(@RequestParam(value = "consultNo", required = false) Integer consultNo,
			HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();

		// 판매 등록 가능한 상담 목록 조회
		List<DealerConsult> completedConsultList = salesService.getCompletedConsultList(dealerNo);

		// 기본 바인딩 객체
		Sales sales = new Sales();

		// 상담번호가 넘어오면 폼 자동 채움
		if (consultNo != null) {
			Sales selectedSales = salesService.createSalesFromConsult(consultNo, dealerNo);

			if (selectedSales != null) {
				sales = selectedSales;
			} else {
				model.addAttribute("errorMessage", "선택한 상담으로 판매 등록을 진행할 수 없습니다.");
			}
		}

		model.addAttribute("consultList", completedConsultList);
		model.addAttribute("completedConsultList", completedConsultList);
		model.addAttribute("selectedConsultNo", consultNo);
		model.addAttribute("sales", sales);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/sales/salesRegister";
	}

	/**
	 * 상담 선택 후 판매 등록 화면 반영
	 * 
	 * URL: GET /dealer/sales/register/select?consultNo=1
	 */
	@GetMapping("/dealer/sales/register/select")
	public String selectConsultForSales(@RequestParam("consultNo") Integer consultNo, HttpSession session) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		return "redirect:/dealer/sales/register?consultNo=" + consultNo;
	}

	/**
	 * 판매 등록 처리
	 * 
	 * URL: POST /dealer/sales/register
	 */
	@PostMapping("/dealer/sales/register")
	public String salesRegister(@RequestParam("consultNo") Integer consultNo,
			@RequestParam("salesAmount") Integer salesAmount, HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();

		String resultMessage = salesService.registerSales(consultNo, dealerNo, salesAmount);

		// 성공 시 판매 목록 이동
		if ("판매 등록이 완료되었습니다.".equals(resultMessage)) {
			return "redirect:/dealer/sales/list";
		}

		// 실패 시 다시 등록 화면 표시
		List<DealerConsult> completedConsultList = salesService.getCompletedConsultList(dealerNo);
		Sales sales = salesService.createSalesFromConsult(consultNo, dealerNo);

		if (sales == null) {
			sales = new Sales();
			sales.setConsultNo(consultNo);
		}

		model.addAttribute("consultList", completedConsultList);
		model.addAttribute("completedConsultList", completedConsultList);
		model.addAttribute("sales", sales);
		model.addAttribute("selectedConsultNo", consultNo);
		model.addAttribute("errorMessage", resultMessage);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/sales/salesRegister";
	}
}