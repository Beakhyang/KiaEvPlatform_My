package com.kiaev.dealer.login;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 딜러 로그인 Repository
 * 
 * 기능: - 사원번호로 딜러 조회 - 사원번호 + 이름으로 딜러 조회(비밀번호 재설정용)
 */
public interface DealerLoginRepository extends JpaRepository<DealerLogin, Integer> {

	/**
	 * 로그인용 사원번호로 딜러 1명 조회
	 */
	Optional<DealerLogin> findByDealerEmpNo(String dealerEmpNo);

	/**
	 * 비밀번호 재설정용 사원번호 + 이름이 모두 일치하는 딜러 조회
	 */
	Optional<DealerLogin> findByDealerEmpNoAndDealerName(String dealerEmpNo, String dealerName);
}