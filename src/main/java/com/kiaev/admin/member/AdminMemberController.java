package com.kiaev.admin.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/list")
    public String list(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("memberList",
                (keyword == null || keyword.isBlank()) ? adminMemberService.getMembers() : adminMemberService.getMembers(keyword));
        return "admin/member/list";
    }

    @GetMapping("/detail/{memberNo}")
    public String detail(@PathVariable("memberNo") Long memberNo, Model model) {
        model.addAttribute("member", adminMemberService.getMember(memberNo));
        return "admin/member/detail";
    }

    @PostMapping("/delete/{memberNo}")
    public String delete(@PathVariable("memberNo") Long memberNo) {
        adminMemberService.deleteMember(memberNo);
        return "redirect:/admin/member/list";
    }
}
