package com.kiaev.admin.dealer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kiaev.dealer.login.DealerLogin;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/dealer")
@RequiredArgsConstructor
public class AdminDealerController {

    private final AdminDealerService adminDealerService;

    @GetMapping("/list")
    public String list(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("dealerList", adminDealerService.getDealers(keyword));
        return "admin/dealer/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("dealer", adminDealerService.createEmptyDealer());
        model.addAttribute("isEdit", false);
        return "admin/dealer/form";
    }

    @GetMapping("/edit/{dealerNo}")
    public String editForm(@PathVariable("dealerNo") Integer dealerNo, Model model) {
        model.addAttribute("dealer", adminDealerService.getDealer(dealerNo));
        model.addAttribute("isEdit", true);
        return "admin/dealer/form";
    }

    @PostMapping("/save")
    public String save(DealerLogin dealer) {
        adminDealerService.save(dealer);
        return "redirect:/admin/dealer/list";
    }

    @PostMapping("/approval/{dealerNo}")
    public String updateApproval(@PathVariable("dealerNo") Integer dealerNo,
                                 @RequestParam("approvalStatus") String approvalStatus) {
        adminDealerService.updateApproval(dealerNo, approvalStatus);
        return "redirect:/admin/dealer/list";
    }

    @PostMapping("/delete/{dealerNo}")
    public String delete(@PathVariable("dealerNo") Integer dealerNo) {
        adminDealerService.delete(dealerNo);
        return "redirect:/admin/dealer/list";
    }
}
