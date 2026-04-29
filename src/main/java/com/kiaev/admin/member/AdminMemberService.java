package com.kiaev.admin.member;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kiaev.client.member.Member;
import com.kiaev.client.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public List<Member> getMembers(String keyword) {
        String normalizedKeyword = normalize(keyword);

        return memberRepository.findAll(Sort.by(Sort.Direction.DESC, "memberNo")).stream()
                .filter(member -> !normalizedKeyword.isBlank())
                .filter(member -> contains(member.getLoginId(), normalizedKeyword)
                        || contains(member.getMemberName(), normalizedKeyword)
                        || contains(member.getEmail(), normalizedKeyword))
                .toList();
    }

    public List<Member> getMembers() {
        return memberRepository.findAll(Sort.by(Sort.Direction.DESC, "memberNo"));
    }

    public Member getMember(Long memberNo) {
        return memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. ID: " + memberNo));
    }

    public void deleteMember(Long memberNo) {
        Member member = getMember(memberNo);
        member.setMemberStatus("탈퇴회원");
        memberRepository.save(member);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String normalize(String keyword) {
        return keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
    }
}
