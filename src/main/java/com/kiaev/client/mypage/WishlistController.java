package com.kiaev.client.mypage;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 마이페이지 - 관심 차량(찜) 컨트롤러
 */
@Controller
@RequestMapping("/mypage/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * 관심 차량 목록 페이지 이동
     */
    @GetMapping
    public String getWishlistPage(HttpSession session, Model model) {
        // 세션에서 로그인 사용자 정보 획득 (Object 타입 유지로 유연성 확보)
        Object loginUser = session.getAttribute("loginUser");
        
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 사용자의 찜 목록 조회 및 모델 등록
        List<Wishlist> wishList = wishlistService.getMyWishlist(loginUser);
        model.addAttribute("wishList", wishList);
        
        return "client/mypage/wishlist"; 
    }

    /**
     * 관심 차량 등록 및 해제 (AJAX)
     * @param carNo 대상 차량 번호
     * @return added(등록), removed(해제), login_required(미로그인), error(오류)
     */
    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<String> toggleWishlist(@RequestParam("carNo") Long carNo, HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        
        // 로그인 체크
        if (loginUser == null) {
            return ResponseEntity.status(401).body("login_required");
        }

        try {
            // 서비스 로직 실행 (DB 상태에 따라 추가/삭제 처리)
            String result = wishlistService.toggleWishlist(loginUser, carNo);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("error");
        }
    }
}