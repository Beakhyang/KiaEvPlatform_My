package com.kiaev.client.mypage;

import com.kiaev.client.car.Car;
import com.kiaev.client.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // 중복 확인: 기존에 등록되었던 관심 차량인지 확인
    boolean existsByMemberAndCar(Member member, Car car);

    // 목록 조회: 이 회원이 찜한 모든 리스트 (최신순)
    List<Wishlist> findByMemberOrderByCreatedAtDesc(Member member);

    // 삭제: 특정 회원과 특정 차량의 연결 고리 제거
    void deleteByMemberAndCar(Member member, Car car);
    // 추가
    Wishlist findByMember_MemberNoAndCar_CarNo(Long memberNo, Long carNo);

    // 추가
    Wishlist findByWishlistNoAndMember_MemberNo(Long wishlistNo, Long memberNo);
}

