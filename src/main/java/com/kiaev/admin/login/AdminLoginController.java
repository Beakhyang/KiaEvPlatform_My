package com.kiaev.admin.login;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminLoginController {

    private final AdminService adminService;

    @GetMapping("/admin")
    public String adminRoot(HttpSession session) {
        return session.getAttribute("loginAdmin") == null ? "redirect:/admin/login" : "redirect:/admin/main";
    }

    @GetMapping("/admin/login")
    public String loginPage() {
        return "admin/login/loginForm";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam("adminId") String adminId,
                        @RequestParam("adminPw") String adminPw,
                        HttpSession session,
                        Model model) {

        Admin loginAdmin = adminService.login(adminId, adminPw);
        if (loginAdmin == null) {
            model.addAttribute("loginError", "관리자 아이디 또는 비밀번호를 확인해 주세요.");
            return "admin/login/loginForm";
        }

        session.setAttribute("loginAdmin", loginAdmin);
        session.setMaxInactiveInterval(1800);
        return "redirect:/admin/main";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
