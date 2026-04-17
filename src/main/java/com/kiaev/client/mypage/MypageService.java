package com.kiaev.client.mypage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kiaev.client.login.Login;
import com.kiaev.client.login.LoginRepository;

@Service
public class MypageService {

    @Autowired
    private MypageRepository mypageRepository;
    
    @Autowired
    private LoginRepository loginRepository;

    // 내 정보 조회
    public Mypage getMemberInfo(Long memberNo) {
        return mypageRepository.findMemberInfoByMemberNo(memberNo);
    }

    // 내 정보 수정
    public void updateMemberInfo(Mypage mypage) {
        mypageRepository.updateMemberInfo(mypage);
    }

    // 비밀번호 변경
    public void updatePassword(String loginId, String newPw) {
        Login member = loginRepository.findByLoginId(loginId);
        if (member != null) {
            member.setMemberPw(newPw);
            loginRepository.save(member);
        }
    }

    // =========================
    // [추가] 비밀번호 변경 처리
    // - 회원번호로 회원 조회
    // - 현재 비밀번호 일치 여부 확인
    // - 새 비밀번호로 변경 후 저장
    // - 성공:true / 실패:false 반환
    // =========================
    public boolean changePassword(Long memberNo, String currentPw, String newPw) {
        
        // 회원번호로 로그인 회원 조회
        Login member = loginRepository.findById(memberNo).orElse(null);

        // 회원 정보가 없으면 실패
        if (member == null) {
            return false;
        }

        // 현재 비밀번호가 일치하지 않으면 실패
        if (!member.getMemberPw().equals(currentPw)) {
            return false;
        }

        // 새 비밀번호로 변경
        member.setMemberPw(newPw);

        // DB 저장
        loginRepository.save(member);

        // 성공
        return true;
    }

    // 회원 탈퇴
    public void deleteMember(Long memberNo) {
        mypageRepository.deleteMember(memberNo);
    }

    // 상담 내역 조회
    public List<Mypage> getConsultHistory(Long memberNo) {
        return mypageRepository.findConsultHistoryByMemberNo(memberNo);
    }

    
    // 문의 내역 조회
    public List<Mypage> getBoardHistory(Long memberNo) {
        return mypageRepository.findBoardHistoryByMemberNo(memberNo);
    }

    // 관심 차량 조회
    public List<Mypage> getFavoriteCars(Long memberNo) {
        return mypageRepository.findFavoriteCarsByMemberNo(memberNo);
    }

    // 관심 차량 삭제
    public void deleteWishlist(Long wishlistNo, Long memberNo) {
        mypageRepository.deleteWishlist(wishlistNo, memberNo);
    }

    public List<Wishlist> getWishlist(Long memberNo) {
        return mypageRepository.getWishlist(memberNo);
    }

    public void toggleWishlist(Long memberNo, Long carId) {
        mypageRepository.toggleWishlist(memberNo, carId);
    }
}