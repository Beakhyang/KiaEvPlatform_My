package com.kiaev.client.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);

    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
}
