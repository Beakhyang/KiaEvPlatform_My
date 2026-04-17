package com.kiaev.client.login;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {

    // 아이디 찾기
    Login findByMemberNameAndEmail(String memberName, String email);

    // 비밀번호 찾기용 회원 확인
    Login findByLoginIdAndMemberNameAndEmail(String loginId, String memberName, String email);

    // 로그인 / 비밀번호 변경용
    Login findByLoginId(String loginId);
}