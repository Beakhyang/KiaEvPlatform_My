package com.kiaev.client.main;

import com.kiaev.client.board.Board;
import com.kiaev.client.board.BoardRepository;
import com.kiaev.client.car.Car;
import com.kiaev.client.car.CarService;
import com.kiaev.client.promotion.Promotion;
import com.kiaev.client.promotion.PromotionRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    // 의존성 주입: 필요한 서비스와 레포지토리를 가져옴
    private final CarService carService;
    private final PromotionRepository promotionRepository;
    private final BoardRepository boardRepository;

    /**
     * [메인 페이지]
     * 언어 설정 처리, 베스트 카 라인업 추출, 메인 프로모션 데이터 준비
     */
    @GetMapping("/main")
    public String mainPage(@RequestParam(value = "lang", required = false) String lang, 
                           HttpSession session, Model model) { 
        
        // 1. 언어 설정: 사용자가 선택한 언어(lang)가 있으면 세션에 저장 (기본값: kr)
        if (lang != null) {
            session.setAttribute("lang", lang.toLowerCase());
        }

        // 2. 차량 라인업 가공: DB의 모든 차 중 Ray EV, EV6, EV9만 골라 가격순 정렬
        List<Car> allCars = carService.findAll(); 
        List<Car> bestLineup = allCars.stream()
                .filter(car -> List.of("Ray EV", "EV6", "EV9").contains(car.getModelName())) 
                // Comparator.nullsLast: 가격 데이터가 null인 경우 리스트 맨 뒤로 보내서 에러 방지
                .sorted(Comparator.comparing(Car::getPrice, Comparator.nullsLast(Comparator.naturalOrder()))) 
                .collect(Collectors.toList());
        model.addAttribute("carLineup", bestLineup);

        // 3. 메인용 프로모션: 최신순으로 가져와서 활성화 상태인 것 3개만 추출
        List<Promotion> promoList = promotionRepository.findAllByOrderByIdDesc().stream()
                .filter(p -> p.isActive() && 
                             (p.getEndDate().isAfter(LocalDateTime.now()) || p.getEndDate().isEqual(LocalDateTime.now())))
                .limit(3)
                .collect(Collectors.toList());
        model.addAttribute("promoList", promoList);

        // 영어 모드일 경우 전용 페이지로, 아니면 한글 메인으로 반환
        if ("en".equals((String)session.getAttribute("lang"))) return "common/englishCatalog";
        return "client/main/main"; 
    }
    
    /**
     * [동적 팝업창]
     * 팝업에 표시할 공지사항 2개와 프로모션 1개를 안전하게 추출
     */
    @GetMapping("/popup")
    public String showPopup(Model model) {
        try {
            LocalDateTime now = LocalDateTime.now();

            /**
             * 1. 팝업용 공지사항 가공
             * - filter: 'NOTICE' 타입이고 '삭제되지 않은(N)' 글만 필터링
             * - sorted: 생성일자(CreatedAt) 기준 내림차순(최신순) 정렬
             * - nullsLast: 날짜 데이터가 없는 더미 데이터가 있어도 서버가 터지지 않게 방어
             * - limit(2): 팝업 공간을 고려해 최신글 2개만 선택
             */
            List<Board> popupNotices = boardRepository.findAll().stream()
                    .filter(b -> "NOTICE".equals(b.getBoardType()) && "N".equals(b.getDeletedYn()))
                    .sorted(Comparator.comparing(Board::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(2)
                    .collect(Collectors.toList());

            /**
             * 2. 팝업용 프로모션 가공
             * - findByIsActiveTrue...: 현재 진행 기간 내에 있는 활성화된 프로모션 조회
             * - sorted/limit(1): 그 중 가장 최근에 등록된 혜택 1개만 선택
             */
            List<Promotion> popupPromos = promotionRepository
                    .findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByBannerOrderAsc(now, now)
                    .stream()
                    .sorted(Comparator.comparing(Promotion::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(1)
                    .collect(Collectors.toList());

            // 가공된 데이터를 팝업 HTML(Model)로 전달
            model.addAttribute("noticeList", popupNotices);
            model.addAttribute("promoList", popupPromos);

        } catch (Exception e) {
            /**
             * [예외 방어]
             * DB 데이터 문제 등으로 에러 발생 시, 사용자에게 500 에러 페이지 대신
             * 데이터가 비어있는 클린한 팝업창을 보여주도록 처리
             */
            model.addAttribute("noticeList", Collections.emptyList());
            model.addAttribute("promoList", Collections.emptyList());
            System.err.println("팝업 데이터 로딩 중 오류 발생: " + e.getMessage());
        }

        return "client/main/popup"; 
    }
}
