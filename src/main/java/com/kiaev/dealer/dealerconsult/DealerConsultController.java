package com.kiaev.dealer.dealerconsult;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kiaev.dealer.login.DealerLogin;

import jakarta.servlet.http.HttpSession;

/**
 * 딜러 상담 관리 Controller
 */
@Controller
public class DealerConsultController {

	@Autowired
	private DealerConsultService consultService;

	@GetMapping("/dealer/consult/list")
	public String consultList(HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		List<DealerConsultView> consultList = consultService.getConsultList(dealerNo);

		model.addAttribute("consultList", consultList);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/consult/consultList";
	}

	@GetMapping("/dealer/consult/detail")
	public String consultDetail(@RequestParam("consultNo") Integer consultNo, HttpSession session, Model model) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		DealerConsultView consult = consultService.getConsultDetail(consultNo, dealerNo);

		if (consult == null) {
			return "redirect:/dealer/consult/list";
		}

		model.addAttribute("consult", consult);
		model.addAttribute("loginDealer", loginDealer);

		return "dealer/consult/consultDetail";
	}

	@PostMapping("/dealer/consult/status")
	public String updateConsultStatus(@RequestParam("consultNo") Integer consultNo,
			@RequestParam("consultStatus") String consultStatus, HttpSession session,
			RedirectAttributes redirectAttributes) {

		DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");
		if (loginDealer == null) {
			return "redirect:/dealer/login";
		}

		Integer dealerNo = loginDealer.getDealerNo();
		boolean updated = consultService.updateConsultStatus(consultNo, dealerNo, consultStatus);

		if (!updated) {
			redirectAttributes.addFlashAttribute("errorMessage", "상담 상태 변경에 실패했습니다.");
			return "redirect:/dealer/consult/detail?consultNo=" + consultNo;
		}

		String successMessage = "상담 상태가 변경되었습니다.";
		if ("COMPLETED".equals(consultStatus)) {
			successMessage = "상담 상태가 완료로 변경되었습니다. 필요하면 판매 등록을 진행해 주세요.";
		}

		redirectAttributes.addFlashAttribute("successMessage", successMessage);
		return "redirect:/dealer/consult/detail?consultNo=" + consultNo;
	}
}
