package com.kiaev.dealer.login;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ========================================================= 딜러 로그인 Service
 * =========================================================
 *
 * 주요 기능 1. 딜러 로그인 처리 2. 딜러 비밀번호 재설정 처리
 *
 * 변경 사항: - 기존 비밀번호 정책 6자 이상 12자 이하 - 변경 비밀번호 정책 4자 이상 6자 이하
 *
 * 주의: - 기존 기능은 제거하지 않음 - 기존 로그인/비밀번호 변경 흐름 유지 - 상수값만 바꾸어서 Controller 와 Service
 * 모두 동일 정책 적용
 */
@Service
public class DealerLoginService {

	// =========================================================
	// 비밀번호 정책 상수
	// 기존: 6자 이상 12자 이하
	// 변경: 4자 이상 6자 이하
	// =========================================================
	public static final int MIN_PASSWORD_LENGTH = 4;
	public static final int MAX_PASSWORD_LENGTH = 6;

	private final DealerLoginRepository dealerLoginRepository;

	/**
	 * ========================================================= 생성자 주입
	 * =========================================================
	 */
	public DealerLoginService(DealerLoginRepository dealerLoginRepository) {
		this.dealerLoginRepository = dealerLoginRepository;
	}

	/**
	 * ========================================================= 딜러 로그인 처리
	 * =========================================================
	 *
	 * 처리 조건 1. 사원번호 존재 2. 계정상태 ACTIVE 3. 승인상태 APPROVED 4. 비밀번호 일치
	 *
	 * 기존 기능 유지: - 사원번호 null/공백 방지 - 비밀번호 null 방지 - 사원번호 trim 처리 - 딜러 조회 - 상태/승인 여부
	 * 체크 - 비밀번호 평문 비교
	 */
	public DealerLogin login(String dealerEmpNo, String dealerPw) {

		// =====================================================
		// 사원번호 null 또는 공백 방지
		// - 기존 기능 유지
		// =====================================================
		if (dealerEmpNo == null || dealerEmpNo.trim().isEmpty()) {
			return null;
		}

		// =====================================================
		// 비밀번호 null 방지
		// - 기존 기능 유지
		// =====================================================
		if (dealerPw == null) {
			return null;
		}

		// =====================================================
		// 사원번호 앞뒤 공백 제거
		// - 기존 기능 유지
		// =====================================================
		dealerEmpNo = dealerEmpNo.trim();

		// =====================================================
		// 사원번호로 딜러 조회
		// - 기존 기능 유지
		// =====================================================
		Optional<DealerLogin> optionalDealer = dealerLoginRepository.findByDealerEmpNo(dealerEmpNo);

		// =====================================================
		// 사원번호가 없으면 로그인 실패
		// - 기존 기능 유지
		// =====================================================
		if (optionalDealer.isEmpty()) {
			return null;
		}

		DealerLogin dealer = optionalDealer.get();

		// =====================================================
		// 계정 상태 체크
		// - 기존 기능 유지
		// =====================================================
		if (!"ACTIVE".equalsIgnoreCase(dealer.getDealerStatus())) {
			return null;
		}

		// =====================================================
		// 승인 상태 체크
		// - 기존 기능 유지
		// =====================================================
		if (!"APPROVED".equalsIgnoreCase(dealer.getApprovalStatus())) {
			return null;
		}

		// =====================================================
		// 비밀번호 체크
		// - 기존 기능 유지
		// - 현재는 평문 비교 방식
		// =====================================================
		if (!dealerPw.equals(dealer.getDealerPw())) {
			return null;
		}

		// =====================================================
		// 로그인 성공
		// - 기존 기능 유지
		// =====================================================
		return dealer;
	}

	/**
	 * ========================================================= 비밀번호 재설정 처리
	 * =========================================================
	 *
	 * 처리 조건 1. 사원번호 / 이름 일치 2. 새 비밀번호와 확인 비밀번호 일치 3. 새 비밀번호 4자 이상 6자 이하 4. 공백 문자열
	 * 불가
	 *
	 * 기존 기능 유지: - null 체크 - trim 처리 - 공백 체크 - 비밀번호 일치 체크 - 길이 체크 - 사원번호 + 이름 본인 확인
	 * - DB 저장
	 */
	@Transactional
	public boolean changePassword(String dealerEmpNo, String dealerName, String newPassword, String confirmPassword) {

		// =====================================================
		// 기본 null 체크
		// - 기존 기능 유지
		// =====================================================
		if (dealerEmpNo == null || dealerName == null || newPassword == null || confirmPassword == null) {
			return false;
		}

		// =====================================================
		// 앞뒤 공백 제거
		// - 기존 기능 유지
		// =====================================================
		dealerEmpNo = dealerEmpNo.trim();
		dealerName = dealerName.trim();
		newPassword = newPassword.trim();
		confirmPassword = confirmPassword.trim();

		// =====================================================
		// 사원번호 / 이름 공백 방지
		// - 기존 기능 유지
		// =====================================================
		if (dealerEmpNo.isEmpty() || dealerName.isEmpty()) {
			return false;
		}

		// =====================================================
		// 새 비밀번호 / 확인 비밀번호 공백 방지
		// - 기존 기능 유지
		// =====================================================
		if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
			return false;
		}

		// =====================================================
		// 새 비밀번호와 확인 비밀번호가 다르면 실패
		// - 기존 기능 유지
		// =====================================================
		if (!newPassword.equals(confirmPassword)) {
			return false;
		}

		// =====================================================
		// 비밀번호 최소 / 최대 길이 체크
		// - 기존 기능 유지
		// - 상수 기준만 4~6으로 변경
		// =====================================================
		if (newPassword.length() < MIN_PASSWORD_LENGTH || newPassword.length() > MAX_PASSWORD_LENGTH) {
			return false;
		}

		// =====================================================
		// 사원번호 + 이름으로 본인 확인
		// - 기존 기능 유지
		// =====================================================
		Optional<DealerLogin> optionalDealer = dealerLoginRepository.findByDealerEmpNoAndDealerName(dealerEmpNo,
				dealerName);

		// =====================================================
		// 일치하는 딜러 정보가 없으면 실패
		// - 기존 기능 유지
		// =====================================================
		if (optionalDealer.isEmpty()) {
			return false;
		}

		DealerLogin dealer = optionalDealer.get();

		// =====================================================
		// 새 비밀번호 저장
		// - 기존 기능 유지
		// - 현재는 평문 저장 방식
		// =====================================================
		dealer.setDealerPw(newPassword);

		// =====================================================
		// DB 저장
		// - 기존 기능 유지
		// =====================================================
		dealerLoginRepository.save(dealer);

		return true;
	}
}