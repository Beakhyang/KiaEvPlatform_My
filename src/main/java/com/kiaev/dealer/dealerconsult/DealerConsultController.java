package com.kiaev.dealer.dealerconsult;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kiaev.dealer.login.DealerLogin;

import jakarta.servlet.http.HttpSession;

/**
 * 상담 관리 Controller
 * 
 * 기능 - 상담 목록 조회 - 상담 상세 조회 - 상담 상태 변경
 * 
 * 기존 기능 유지
 */
@Controller
public class DealerConsultController {

	@Autowired
	private DealerConsultService consultService;

	/**
	 * 상담 목록 조회
	 * 
	 * URL: GET /dealer/consult/list
	 */
	@GetMapping("/dealer/consult/list")
	public String consultList(HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		List<DealerConsult> consultList = consultService.getConsultList(dealerNo);

		model.addAttribute("consultList", consultList);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/consult/consultList";
	}

	/**
	 * 상담 상세 조회
	 * 
	 * URL: GET /dealer/consult/detail?consultNo=1
	 */
	@GetMapping("/dealer/consult/detail")
	public String consultDetail(@RequestParam("consultNo") Integer consultNo, HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		DealerConsult consult = consultService.getConsultDetail(consultNo, dealerNo);

		if (consult == null) {
			return "redirect:/dealer/consult/list";
		}

		model.addAttribute("consult", consult);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/consult/consultDetail";
	}

	/**
	 * 상담 상태 변경
	 * 
	 * URL: POST /dealer/consult/status
	 * 
	 * 핵심 흐름 - 완료로 변경 시 판매 등록 화면으로 즉시 이동 - 그 외 상태는 다시 상세 화면으로 이동
	 */
	@PostMapping("/dealer/consult/status")
	public String updateConsultStatus(@RequestParam("consultNo") Integer consultNo,
			@RequestParam("consultStatus") String consultStatus, HttpSession session) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");

		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();

		// 상담 상태 변경
		consultService.updateConsultStatus(consultNo, dealerNo, consultStatus);

		// 완료 상태면 판매 등록 화면으로 이동
		if ("완료".equals(consultStatus) || "COMPLETED".equals(consultStatus)) {
			return "redirect:/dealer/sales/register/select?consultNo=" + consultNo;
		}

		// 완료가 아니면 다시 상세로
		return "redirect:/dealer/consult/detail?consultNo=" + consultNo;
	}
}