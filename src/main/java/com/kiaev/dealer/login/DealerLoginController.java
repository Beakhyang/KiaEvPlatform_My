package com.kiaev.dealer.login;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class DealerLoginController {

    private final DealerLoginService dealerLoginService;

    public DealerLoginController(DealerLoginService dealerLoginService) {
        this.dealerLoginService = dealerLoginService;
    }

    @GetMapping({ "/dealer", "/dealer/" })
    public String dealerEntry(HttpSession session) {
        DealerLogin loginDealer = (DealerLogin) session.getAttribute("loginDealer");
        if (loginDealer != null) {
            return "redirect:/dealer/main";
        }

        return "redirect:/dealer/login";
    }

    @GetMapping("/dealer/login")
    public String dealerLoginPage() {
        return "dealer/login/DealerLogin";
    }

    @PostMapping("/dealer/login")
    public String dealerLogin(@RequestParam("dealerEmpNo") String dealerEmpNo,
            @RequestParam("dealerPw") String dealerPw, HttpSession session, Model model) {

        DealerLogin loginDealer = dealerLoginService.login(dealerEmpNo, dealerPw);

        if (loginDealer == null) {
            model.addAttribute("errorMessage", "사원번호 또는 비밀번호를 확인해주세요.");
            return "dealer/login/DealerLogin";
        }

        session.setAttribute("loginDealer", loginDealer);
        return "redirect:/dealer/main";
    }

    @GetMapping("/dealer/password/reset")
    public String passwordResetPage() {
        return "dealer/login/passwordReset";
    }

    @PostMapping("/dealer/password/reset")
    public String passwordReset(@RequestParam("dealerEmpNo") String dealerEmpNo,
            @RequestParam("dealerName") String dealerName, @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword, Model model) {

        dealerEmpNo = dealerEmpNo == null ? "" : dealerEmpNo.trim();
        dealerName = dealerName == null ? "" : dealerName.trim();
        newPassword = newPassword == null ? "" : newPassword.trim();
        confirmPassword = confirmPassword == null ? "" : confirmPassword.trim();

        if (dealerEmpNo.isEmpty() || dealerName.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            model.addAttribute("errorMessage", "모든 항목을 입력해주세요.");
            return "dealer/login/passwordReset";
        }

        if (newPassword.length() < DealerLoginService.MIN_PASSWORD_LENGTH
                || newPassword.length() > DealerLoginService.MAX_PASSWORD_LENGTH) {

            model.addAttribute("errorMessage", "새 비밀번호는 " + DealerLoginService.MIN_PASSWORD_LENGTH + "자 이상 "
                    + DealerLoginService.MAX_PASSWORD_LENGTH + "자 이하로 입력해주세요.");

            return "dealer/login/passwordReset";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
            return "dealer/login/passwordReset";
        }

        boolean result = dealerLoginService.changePassword(dealerEmpNo, dealerName, newPassword, confirmPassword);

        if (!result) {
            model.addAttribute("errorMessage", "사원번호 또는 이름이 일치하지 않아 비밀번호 변경에 실패했습니다.");
            return "dealer/login/passwordReset";
        }

        model.addAttribute("successMessage", "비밀번호가 변경되었습니다. 다시 로그인해주세요.");
        return "dealer/login/DealerLogin";
    }

    @GetMapping("/dealer/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/dealer/login";
    }
}
