package com.kiaev.client.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller // 스프링에게 이 클래스가 웹 요청을 처리하는 '컨트롤러' 역할임을 알려줍니다.
@RequestMapping("/promotion") // 이 컨트롤러의 모든 주소는 기본적으로 '/promotion'으로 시작하게 만듭니다.
@RequiredArgsConstructor // Service 객체를 자동으로 주입(연결)해 줍니다.
public class PromotionController {

    // 서비스 클래스의 로직을 사용하기 위해 불러옵니다.
    private final PromotionService promotionService;

    /**
     * [프로모션 목록 화면 조회]
     * 사용자가 브라우저에 'GET /promotion/list' 주소를 입력하면 실행됩니다.
     */
    @GetMapping("/list")
    public String promotionList(Model model) {
        // 1. 서비스에서 '진행 중인 프로모션 목록' 데이터를 가져옵니다.
        List<Promotion> promotions = promotionService.getActivePromotions();

        
        // 2. 가져온 데이터를 HTML 파일(뷰)로 전달하기 위해 Model에 담습니다.
        // 이름표("promotions")를 달아서 넘겨주면, HTML에서 이 이름표로 데이터를 꺼내 쓸 수 있습니다.
        model.addAttribute("promotions", promotions);
        
        // 3. 사용자에게 보여줄 HTML 파일의 경로를 반환합니다.
        // (src/main/resources/templates/client/promotion/promotionList.html 파일을 찾아 화면에 띄웁니다)
        return "client/promotion/promotionList"; 
    }

    /**
     * [프로모션 상세 화면 조회]
     * 사용자가 특정 프로모션을 클릭하여 'GET /promotion/detail?id=1' 처럼 요청하면 실행됩니다.
     */
    @GetMapping("/detail")
    public String promotionDetail(@RequestParam("id") Long id, Model model) {
        // @RequestParam("id"): 주소창의 ?id=1 부분에서 '1'이라는 값을 변수 id에 쏙 넣어줍니다.

        // 1. 전달받은 id를 이용해 서비스에서 해당 프로모션의 상세 데이터를 가져옵니다.
        Promotion promotion = promotionService.getPromotionById(id);
        
        // 2. HTML 파일로 전달하기 위해 Model에 "promotion"이라는 이름으로 데이터를 담습니다.
        model.addAttribute("promotion", promotion);
        
        // 3. 보여줄 상세 페이지 HTML 파일의 경로를 반환합니다.
        // (src/main/resources/templates/client/promotion/promotionDetail.html)
        return "client/promotion/promotionDetail"; 
    }
}