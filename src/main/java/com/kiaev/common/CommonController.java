package com.kiaev.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {

    @GetMapping("/")
    public String index() { return "redirect:/main"; }

    @GetMapping("/company") // 회사정보
    public String company() { return "common/company"; }

    @GetMapping("/terms") // 이용약관
    public String terms() { return "common/terms"; }

    @GetMapping("/privacy") // 개인정보처리방침
    public String privacy() { return "common/privacy"; }
}