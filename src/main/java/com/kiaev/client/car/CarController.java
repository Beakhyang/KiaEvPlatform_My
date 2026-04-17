package com.kiaev.client.car;

import com.kiaev.client.mypage.Wishlist;
import com.kiaev.client.mypage.WishlistService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/car")
@RequiredArgsConstructor
public class CarController {
    
    private final CarService carService;
    private final WishlistService wishlistService; 

    // 차량 목록 페이지 조회
    @GetMapping("/list")
    public String carList(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "carType", required = false) String carType,
        @RequestParam(value = "sort", required = false) String sort,
        HttpSession session, Model model) {

        List<Car> cars = carService.searchCars(keyword, carType, sort);

        Object loginUser = session.getAttribute("loginUser");
        Set<Long> wishCarNos = new HashSet<>();
        
        if (loginUser != null) {
            List<Wishlist> myWishes = wishlistService.getMyWishlist(loginUser);
            for (Wishlist wish : myWishes) {
                wishCarNos.add(wish.getCar().getCarNo());
            }
        }

        model.addAttribute("cars", cars);
        model.addAttribute("wishCarNos", wishCarNos);
        model.addAttribute("keyword", keyword);
        model.addAttribute("carType", carType);
        model.addAttribute("sort", sort);
        
        return "client/car/carList";
    }
    
    // 차량 상세 정보 페이지 조회
    @GetMapping("/detail/{carNo}")
    public String carDetail(@PathVariable("carNo") Long carNo, HttpSession session, Model model) {
        // 1. 차량 데이터 조회
        Car car = carService.findCarById(carNo);
        
        // 2. 로그인 사용자 관심 차량 확인 로직 추가
        Object loginUser = session.getAttribute("loginUser");
        boolean isWish = false;
        
        if (loginUser != null) {

            isWish = wishlistService.isFavorite(loginUser, carNo);
        }
        
        model.addAttribute("car", car);
        model.addAttribute("isWish", isWish); // 화면으로 전달
        
        return "client/car/carDetail";
    }
}