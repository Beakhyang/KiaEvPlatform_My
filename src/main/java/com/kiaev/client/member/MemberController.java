package com.kiaev.client.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    // 약관동의 화면
    @GetMapping("/member/terms")
    public String termsForm() {
        return "client/member/termsForm";
    }

    // 약관동의 처리
    @PostMapping("/member/terms")
    public String agreeTerms(
            @RequestParam(value = "agreeService", required = false) String agreeService,
            @RequestParam(value = "agreePrivacy", required = false) String agreePrivacy,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 필수 약관 미동의 시 다시 약관 페이지로 이동
        if (agreeService == null || agreePrivacy == null) {
            redirectAttributes.addFlashAttribute("termsError", "필수 약관에 동의해야 회원가입이 가능합니다.");
            return "redirect:/member/terms";
        }

        // 약관동의 여부를 세션에 저장
        session.setAttribute("termsAgreed", true);

        // 회원가입 페이지로 이동
        return "redirect:/member/join";
    }

    // 회원가입 화면
    @GetMapping("/member/join")
    public String joinForm(HttpSession session, RedirectAttributes redirectAttributes) {
        Boolean termsAgreed = (Boolean) session.getAttribute("termsAgreed");

        // 약관동의 없이 회원가입 페이지 직접 접근 방지
        if (termsAgreed == null || !termsAgreed) {
            redirectAttributes.addFlashAttribute("termsError", "약관동의 후 회원가입이 가능합니다.");
            return "redirect:/member/terms";
        }

        return "client/member/joinForm";
    }

    // 회원가입 저장
    @PostMapping("/member/join")
    public String join(Member member, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            memberService.join(member);

            // 회원가입 완료 후 약관동의 세션 제거
            session.removeAttribute("termsAgreed");

            // 회원가입 성공 메시지 전달
            // redirect 시 1회성으로 로그인 페이지에 전달됨
            redirectAttributes.addFlashAttribute("joinSuccess", "회원가입이 완료되었습니다. 로그인해주세요.");

            // 회원가입 성공 후 로그인 페이지로 이동
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            // 실패 메시지 전달
            redirectAttributes.addFlashAttribute("joinError", e.getMessage());

            // 실패 -> 회원가입 페이지
            return "redirect:/member/join";

        } catch (Exception e) {
            e.printStackTrace();

            // 오류 메시지 전달
            redirectAttributes.addFlashAttribute("joinError", "회원가입 중 오류가 발생했습니다.");

            // 오류 -> 회원가입 페이지
            return "redirect:/member/join";
        }
    }

    // 아이디 중복확인
    @GetMapping("/member/checkId")
    @ResponseBody
    public Map<String, Object> checkId(@RequestParam("loginId") String loginId) {
        boolean exists = memberService.existsByLoginId(loginId);

        Map<String, Object> result = new HashMap<>();
        result.put("duplicate", exists);
        result.put("message", exists ? "이미 사용중인 아이디 입니다." : "사용 가능한 아이디 입니다.");

        return result;
    }

    // 이메일 중복확인
    @GetMapping("/member/checkEmail")
    @ResponseBody
    public Map<String, Object> checkEmail(@RequestParam("email") String email) {
        boolean exists = memberService.existsByEmail(email);

        Map<String, Object> result = new HashMap<>();
        result.put("duplicate", exists);
        result.put("message", exists ? "이미 사용중인 이메일 입니다." : "사용 가능한 이메일 입니다.");

        return result;
    }
}