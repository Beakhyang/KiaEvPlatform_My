package com.kiaev.cbadmin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kiaev.cbclient.ChatbotFaq;
import com.kiaev.cbclient.ChatbotFaqService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/chatbot")
@RequiredArgsConstructor
public class ChatbotFaqAdminController {

    private final ChatbotFaqService chatbotFaqService;

    @GetMapping("/faq/list")
    public String list(Model model) {
        model.addAttribute("faqList", chatbotFaqService.getAllFaqs());
        return "admin/cbadmin/faqList";
    }

    @GetMapping("/faq/new")
    public String createForm(Model model) {
        ChatbotFaq faq = new ChatbotFaq();
        faq.setActiveYn("Y");
        faq.setDisplayOrder(0);
        model.addAttribute("faq", faq);
        model.addAttribute("isEdit", false);
        return "admin/cbadmin/faqForm";
    }

    @GetMapping("/faq/edit/{faqNo}")
    public String editForm(@PathVariable("faqNo") Long faqNo, Model model) {
        model.addAttribute("faq", chatbotFaqService.getFaq(faqNo));
        model.addAttribute("isEdit", true);
        return "admin/cbadmin/faqForm";
    }

    @PostMapping("/faq/save")
    public String save(ChatbotFaq faq) {
        chatbotFaqService.save(faq);
        return "redirect:/admin/chatbot/faq/list";
    }

    @PostMapping("/faq/delete/{faqNo}")
    public String delete(@PathVariable("faqNo") Long faqNo) {
        chatbotFaqService.delete(faqNo);
        return "redirect:/admin/chatbot/faq/list";
    }
}
