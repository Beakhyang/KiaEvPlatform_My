package com.kiaev.client.main;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kiaev.client.board.Board;
import com.kiaev.client.board.BoardRepository;
import com.kiaev.client.car.Car;
import com.kiaev.client.car.CarService;
import com.kiaev.client.promotion.Promotion;
import com.kiaev.client.promotion.PromotionImagePathNormalizer;
import com.kiaev.client.promotion.PromotionRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CarService carService;
    private final PromotionRepository promotionRepository;
    private final PromotionImagePathNormalizer promotionImagePathNormalizer;
    private final BoardRepository boardRepository;

    @GetMapping("/main")
    public String mainPage(@RequestParam(value = "lang", required = false) String lang,
            HttpSession session, Model model) {

        if (lang != null) {
            session.setAttribute("lang", lang.toLowerCase());
        }

        List<Car> allCars = carService.findAll();
        List<Car> bestLineup = allCars.stream()
                .filter(car -> List.of("Ray EV", "EV6", "EV9").contains(car.getModelName()))
                .sorted(Comparator.comparing(Car::getPrice, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        model.addAttribute("carLineup", bestLineup);

        LocalDateTime now = LocalDateTime.now();

        List<Promotion> promoList = promotionRepository.findAllByOrderByIdDesc().stream()
                .filter(p -> !"POPUP".equalsIgnoreCase(p.getType()))
                .filter(p -> p.isActive()
                        && p.getStartDate() != null
                        && p.getEndDate() != null
                        && (p.getStartDate().isBefore(now) || p.getStartDate().isEqual(now))
                        && (p.getEndDate().isAfter(now) || p.getEndDate().isEqual(now)))
                .limit(3)
                .map(promotionImagePathNormalizer::normalize)
                .collect(Collectors.toList());
        model.addAttribute("promoList", promoList);

        if ("en".equals((String) session.getAttribute("lang"))) {
            return "common/englishCatalog";
        }
        return "client/main/main";
    }

    @GetMapping("/popup")
    public String showPopup(Model model) {
        try {
            LocalDateTime now = LocalDateTime.now();

            List<Board> popupNotices = boardRepository.findAll().stream()
                    .filter(b -> "NOTICE".equalsIgnoreCase(b.getBoardType()))
                    .filter(b -> "N".equalsIgnoreCase(b.getDeletedYn()))
                    .filter(b -> !"Y".equalsIgnoreCase(b.getHidden()))
                    .sorted(Comparator
                            .comparing(Board::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(Board::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(2)
                    .collect(Collectors.toList());

            List<Promotion> popupPromos = promotionRepository
                    .findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByBannerOrderAsc(now, now)
                    .stream()
                    .filter(promo -> "POPUP".equalsIgnoreCase(promo.getType()))
                    .sorted(Comparator.comparing(Promotion::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(1)
                    .map(promotionImagePathNormalizer::normalize)
                    .collect(Collectors.toList());

            model.addAttribute("noticeList", popupNotices);
            model.addAttribute("promoList", popupPromos);

        } catch (Exception e) {
            model.addAttribute("noticeList", Collections.emptyList());
            model.addAttribute("promoList", Collections.emptyList());
            System.err.println("팝업 데이터 로딩 중 오류 발생: " + e.getMessage());
        }

        return "client/main/popup";
    }
}
