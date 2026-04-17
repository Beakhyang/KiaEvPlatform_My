package com.kiaev.client.mypage;

import com.kiaev.client.car.Car;
import com.kiaev.client.car.CarRepository;
import com.kiaev.client.member.Member;
import com.kiaev.client.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Method;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final CarRepository carRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public String toggleWishlist(Object loginUser, Long carNo) {
        Long memberNo = getMemberNoFromSession(loginUser);

        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));

        Car car = carRepository.findById(carNo)
                .orElseThrow(() -> new IllegalArgumentException("차량 정보 없음"));

        if (wishlistRepository.existsByMemberAndCar(member, car)) {
            wishlistRepository.deleteByMemberAndCar(member, car);
            return "removed";
        } else {
            Wishlist wishlist = Wishlist.builder().member(member).car(car).build();
            wishlistRepository.save(wishlist);
            return "added";
        }
    }

    @Transactional(readOnly = true)
    public List<Wishlist> getMyWishlist(Object loginUser) {
        Long memberNo = getMemberNoFromSession(loginUser);
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));
        return wishlistRepository.findByMemberOrderByCreatedAtDesc(member);
    }

    // 상세 페이지 등에서 특정 차량의 찜 여부를 확인하는 메서드
    @Transactional(readOnly = true)
    public boolean isFavorite(Object loginUser, Long carNo) {
        if (loginUser == null) return false;
        
        try {
            Long memberNo = getMemberNoFromSession(loginUser);
            Member member = memberRepository.findById(memberNo).orElse(null);
            Car car = carRepository.findById(carNo).orElse(null);
            
            if (member == null || car == null) return false;
            
            // 사용자와 차량 정보가 모두 있을 때, 찜 테이블에 존재하는지 확인
            return wishlistRepository.existsByMemberAndCar(member, car);
        } catch (Exception e) {
            return false;
        }
    }

    // 세션 객체에서 getMemberNo()를 안전하게 꺼내는 메서드
    private Long getMemberNoFromSession(Object loginUser) {
        try {
            Method method = loginUser.getClass().getMethod("getMemberNo");
            return (Long) method.invoke(loginUser);
        } catch (Exception e) {
            throw new RuntimeException("회원 번호 추출 실패");
        }
    }
}