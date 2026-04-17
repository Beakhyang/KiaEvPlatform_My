package com.kiaev.client.member;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public void join(Member member) {
        // 아이디 중복 체크
        if (memberRepository.existsByLoginId(member.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 기본값 세팅
        if (member.getMemberStatus() == null || member.getMemberStatus().isBlank()) {
            member.setMemberStatus("정상회원");
        }

        if (member.getJoinDate() == null) {
            member.setJoinDate(LocalDate.now());
        }
        
        // 비밀번호 확인 체크
        if (member.getMemberPw() == null || member.getConfirmPw() == null ||
            !member.getMemberPw().equals(member.getConfirmPw())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // 저장
        memberRepository.save(member);
    }

    public void delete(Long memberNo) {
        memberRepository.deleteById(memberNo);
    }

    public boolean existsByLoginId(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }
}