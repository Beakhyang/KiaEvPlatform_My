package com.kiaev.admin.consult;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kiaev.admin.dealer.AdminDealerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/consult")
@RequiredArgsConstructor
public class AdminConsultController {

    private final AdminConsultService adminConsultService;
    private final AdminDealerService adminDealerService;

    @GetMapping("/list")
    public String list(@RequestParam(value = "status", required = false) String status, Model model) {
        model.addAttribute("status", status);
        model.addAttribute("consultList", adminConsultService.getConsultList(status));
        return "admin/consult/list";
    }

    @GetMapping("/detail/{consultNo}")
    public String detail(@PathVariable("consultNo") Long consultNo, Model model) {
        model.addAttribute("consult", adminConsultService.getConsultView(consultNo));
        model.addAttribute("dealerList", adminDealerService.getAssignableDealers());
        return "admin/consult/detail";
    }

    @PostMapping("/update/{consultNo}")
    public String update(@PathVariable("consultNo") Long consultNo,
                         @RequestParam(value = "consultStatus", required = false) String consultStatus,
                         @RequestParam(value = "dealerNo", required = false) Integer dealerNo,
                         @RequestParam(value = "consultMemo", required = false) String consultMemo) {
        adminConsultService.updateConsult(consultNo, consultStatus, dealerNo, consultMemo);
        return "redirect:/admin/consult/detail/" + consultNo;
    }
}
