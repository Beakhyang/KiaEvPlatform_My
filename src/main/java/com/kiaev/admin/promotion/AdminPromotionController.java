package com.kiaev.admin.promotion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kiaev.client.promotion.Promotion;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/promotion")
@RequiredArgsConstructor
public class AdminPromotionController {

    private final AdminPromotionService adminPromotionService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("promotionList", adminPromotionService.getPromotions());
        return "admin/promotion/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("promotion", adminPromotionService.createEmptyPromotion());
        model.addAttribute("isEdit", false);
        return "admin/promotion/form";
    }

    @GetMapping("/edit/{promotionId}")
    public String editForm(@PathVariable("promotionId") Long promotionId, Model model) {
        model.addAttribute("promotion", adminPromotionService.getPromotion(promotionId));
        model.addAttribute("isEdit", true);
        return "admin/promotion/form";
    }

    @PostMapping("/save")
    public String save(Promotion promotion) {
        adminPromotionService.save(promotion);
        return "redirect:/admin/promotion/list";
    }

    @PostMapping("/toggle/{promotionId}")
    public String toggle(@PathVariable("promotionId") Long promotionId) {
        adminPromotionService.toggle(promotionId);
        return "redirect:/admin/promotion/list";
    }

    @PostMapping("/delete/{promotionId}")
    public String delete(@PathVariable("promotionId") Long promotionId) {
        adminPromotionService.delete(promotionId);
        return "redirect:/admin/promotion/list";
    }
}
