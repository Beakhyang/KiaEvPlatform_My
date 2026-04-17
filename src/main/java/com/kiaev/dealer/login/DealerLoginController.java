package com.kiaev.dealer.login;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

/**
 * ========================================================= 딜러 로그인 Controller
 * =========================================================
 *
 * 주요 기능 1. 딜러 로그인 화면 이동 2. 딜러 로그인 처리 3. 비밀번호 재설정 화면 이동 4. 비밀번호 재설정 처리 5. 로그아웃
 * 처리
 *
 * 주의: - 기존 기능은 제거하지 않음 - 비밀번호 길이 정책은 Service 의 상수를 참조함 - 따라서 정책 변경은 Service 상수
 * 변경만으로 반영됨
 */
@Controller
public class DealerLoginController {

	private final DealerLoginService dealerLoginService;

	/**
	 * ========================================================= 생성자 주입
	 * =========================================================
	 */
	public DealerLoginController(DealerLoginService dealerLoginService) {
		this.dealerLoginService = dealerLoginService;
	}

	/**
	 * ========================================================= 딜러 로그인 화면 이동 URL:
	 * GET /dealer/login =========================================================
	 */
	@GetMapping("/dealer/login")
	public String dealerLoginPage() {
		return "dealer/login/DealerLogin";
	}

	/**
	 * ========================================================= 딜러 로그인 처리 URL: POST
	 * /dealer/login =========================================================
	 *
	 * 기존 기능 유지: - 로그인 서비스 호출 - 실패 시 에러 메시지 출력 - 성공 시 세션에 loginDealer 저장 - 딜러 메인
	 * 페이지로 이동
	 */
	@PostMapping("/dealer/login")
	public String dealerLogin(@RequestParam("dealerEmpNo") String dealerEmpNo,
			@RequestParam("dealerPw") String dealerPw, HttpSession session, Model model) {

		// =====================================================
		// 로그인 서비스 호출
		// =====================================================
		DealerLogin loginDealer = dealerLoginService.login(dealerEmpNo, dealerPw);

		// =====================================================
		// 로그인 실패
		// - 기존 기능 유지
		// =====================================================
		if (loginDealer == null) {
			model.addAttribute("errorMessage", "사원번호 또는 비밀번호를 확인해주세요.");
			return "dealer/login/DealerLogin";
		}

		// =====================================================
		// 로그인 성공 시 세션 저장
		// - 기존 기능 유지
		// =====================================================
		session.setAttribute("loginDealer", loginDealer);

		// =====================================================
		// 딜러 메인페이지로 이동
		// - 기존 기능 유지
		// =====================================================
		return "redirect:/dealer/main";
	}

	/**
	 * ========================================================= 비밀번호 재설정 화면 이동 URL:
	 * GET /dealer/password/reset
	 * =========================================================
	 */
	@GetMapping("/dealer/password/reset")
	public String passwordResetPage() {
		return "dealer/login/passwordReset";
	}

	/**
	 * ========================================================= 비밀번호 재설정 처리 URL:
	 * POST /dealer/password/reset
	 * =========================================================
	 *
	 * 기존 기능 유지: - null 방지 / trim 처리 - 공백 검사 - 비밀번호 길이 검사 - 새 비밀번호 / 확인 비밀번호 일치 검사 -
	 * 서비스 호출 - 실패 시 에러 메시지 - 성공 시 로그인 화면에 successMessage 출력
	 *
	 * 변경 포인트: - 길이 정책은 Service 상수값을 참조 - Service 상수를 4~6으로 바꾸면 여기 메시지도 자동 반영됨
	 */
	@PostMapping("/dealer/password/reset")
	public String passwordReset(@RequestParam("dealerEmpNo") String dealerEmpNo,
			@RequestParam("dealerName") String dealerName, @RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword, Model model) {

		// =====================================================
		// null 방지 + 앞뒤 공백 제거
		// - 기존 기능 유지
		// =====================================================
		dealerEmpNo = dealerEmpNo == null ? "" : dealerEmpNo.trim();
		dealerName = dealerName == null ? "" : dealerName.trim();
		newPassword = newPassword == null ? "" : newPassword.trim();
		confirmPassword = confirmPassword == null ? "" : confirmPassword.trim();

		// =====================================================
		// 필수 입력값 공백 체크
		// - 기존 기능 유지
		// =====================================================
		if (dealerEmpNo.isEmpty() || dealerName.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
			model.addAttribute("errorMessage", "모든 항목을 입력해주세요.");
			return "dealer/login/passwordReset";
		}

		// =====================================================
		// 비밀번호 길이 체크
		// - 기존 기능 유지
		// - 길이 기준만 Service 상수값 기준으로 동작
		// - Service 에서 4~6으로 변경하면 여기 메시지도 자동 변경
		// =====================================================
		if (newPassword.length() < DealerLoginService.MIN_PASSWORD_LENGTH
				|| newPassword.length() > DealerLoginService.MAX_PASSWORD_LENGTH) {

			model.addAttribute("errorMessage", "새 비밀번호는 " + DealerLoginService.MIN_PASSWORD_LENGTH + "자 이상 "
					+ DealerLoginService.MAX_PASSWORD_LENGTH + "자 이하로 입력해주세요.");

			return "dealer/login/passwordReset";
		}

		// =====================================================
		// 새 비밀번호 / 새 비밀번호 확인 일치 체크
		// - 기존 기능 유지
		// =====================================================
		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("errorMessage", "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
			return "dealer/login/passwordReset";
		}

		// =====================================================
		// 서비스 호출
		// - 기존 기능 유지
		// =====================================================
		boolean result = dealerLoginService.changePassword(dealerEmpNo, dealerName, newPassword, confirmPassword);

		// =====================================================
		// 비밀번호 변경 실패
		// - 기존 기능 유지
		// =====================================================
		if (!result) {
			model.addAttribute("errorMessage", "사원번호 또는 이름이 일치하지 않아 비밀번호 변경에 실패했습니다.");
			return "dealer/login/passwordReset";
		}

		// =====================================================
		// 비밀번호 변경 성공 후 로그인 화면으로 이동
		// - 기존 기능 유지
		// - 현재 구조에서는 로그인 화면을 바로 반환하여 successMessage 표시
		// =====================================================
		model.addAttribute("successMessage", "비밀번호가 변경되었습니다. 다시 로그인해주세요.");
		return "dealer/login/DealerLogin";
	}

	/**
	 * ========================================================= 로그아웃 처리 URL: GET
	 * /dealer/logout =========================================================
	 *
	 * 기존 기능 유지: - 세션 전체 삭제 - 로그인 페이지로 이동
	 */
	@GetMapping("/dealer/logout")
	public String logout(HttpSession session) {

		// =====================================================
		// 세션 전체 삭제
		// =====================================================
		session.invalidate();

		// =====================================================
		// 로그인 페이지로 이동
		// =====================================================
		return "redirect:/dealer/login";
	}
}