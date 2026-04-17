package com.kiaev.dealer.dealerconsult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 상담 Service
 * 
 * 기능 - 로그인한 딜러의 상담 목록 조회 - 로그인한 딜러의 상담 상세 조회 - 상담 상태 변경 - 대시보드용 상담 건수 조회 - 상담
 * 메모 누적 저장
 * 
 * 상태값은 로직/DB 기준 영문 코드로 통일합니다.
 * 
 * WAITING IN_PROGRESS COMPLETED
 * 
 * 단, 기존 데이터에 한글(대기/진행중/완료)이 남아있을 수 있으므로 조회/판별 시에는 한글도 함께 인식합니다.
 * 
 * 참고: - consult_memo 는 고객 / 관리자 측에서 작성된 메모를 저장하는 컬럼입니다. - 딜러 서비스에서는
 * consult_memo 값을 직접 수정하지 않고 조회만 합니다. - appendConsultMemo() 는 현재 딜러 화면에서는 직접
 * 사용하지 않아도 되지만, 기존 구조를 유지하기 위해 그대로 보존합니다.
 */
@Service
public class DealerConsultService {

	@Autowired
	private DealerConsultRepository consultRepository;

	/**
	 * 상담 메모 누적 저장 시 사용할 날짜 포맷 예: 2026-04-08 10:15:22
	 */
	private static final DateTimeFormatter MEMO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * 로그인한 딜러의 상담 목록 조회
	 * 
	 * @param dealerNo 로그인한 딜러번호
	 * @return 해당 딜러의 상담 목록
	 */
	public List<DealerConsult> getConsultList(Integer dealerNo) {
		return consultRepository.findByDealerNoOrderByConsultNoDesc(dealerNo);
	}

	/**
	 * 로그인한 딜러의 상담 상세 조회
	 * 
	 * 상담번호와 딜러번호가 모두 일치하는 경우만 조회합니다. 다른 딜러의 상담은 조회되지 않도록 처리합니다.
	 * 
	 * @param consultNo 상담번호
	 * @param dealerNo  딜러번호
	 * @return 조회된 상담 객체, 없으면 null
	 */
	public DealerConsult getConsultDetail(Integer consultNo, Integer dealerNo) {

		if (consultNo == null || dealerNo == null) {
			return null;
		}

		Optional<DealerConsult> optionalConsult = consultRepository.findByConsultNoAndDealerNo(consultNo, dealerNo);

		if (optionalConsult.isPresent()) {
			return optionalConsult.get();
		}

		return null;
	}

	/**
	 * 상담 상태 변경
	 * 
	 * 화면에서 한글/영문 어떤 값이 와도 내부적으로는 영문 코드로 저장합니다.
	 * 
	 * 처리 규칙 - WAITING : completedDate 비움 - IN_PROGRESS : assignedDate 현재 시각
	 * 저장(없으면), completedDate 비움 - COMPLETED : assignedDate 없으면 현재 시각 저장,
	 * completedDate 현재 시각 저장
	 * 
	 * @param consultNo     상담번호
	 * @param dealerNo      딜러번호
	 * @param consultStatus 변경할 상담상태
	 */
	public void updateConsultStatus(Integer consultNo, Integer dealerNo, String consultStatus) {

		// 필수값 방어
		if (consultNo == null || dealerNo == null || consultStatus == null) {
			return;
		}

		Optional<DealerConsult> optionalConsult = consultRepository.findByConsultNoAndDealerNo(consultNo, dealerNo);

		// 상담이 없으면 종료
		if (optionalConsult.isEmpty()) {
			return;
		}

		DealerConsult consult = optionalConsult.get();

		// 입력값을 영문 상태코드로 정규화
		String normalizedStatus = normalizeStatus(consultStatus);

		// 지원하지 않는 상태값이면 종료
		if (normalizedStatus == null) {
			return;
		}

		// 상담 상태 변경
		consult.setConsultStatus(normalizedStatus);

		// 진행중으로 변경 시
		if ("IN_PROGRESS".equals(normalizedStatus)) {
			if (consult.getAssignedDate() == null) {
				consult.setAssignedDate(LocalDateTime.now());
			}
			consult.setCompletedDate(null);
		}

		// 완료로 변경 시
		else if ("COMPLETED".equals(normalizedStatus)) {
			if (consult.getAssignedDate() == null) {
				consult.setAssignedDate(LocalDateTime.now());
			}
			consult.setCompletedDate(LocalDateTime.now());
		}

		// 대기로 변경 시
		else if ("WAITING".equals(normalizedStatus)) {
			consult.setCompletedDate(null);

			// 필요 시 배정일시도 초기화 가능
			// consult.setAssignedDate(null);
		}

		consultRepository.save(consult);
	}

	/**
	 * 상담 메모 누적 저장
	 * 
	 * consult_no 기준으로 상담을 찾아서 consult_memo 에 기록을 누적합니다. 현재 딜러 화면에서 직접 쓰지 않아도, 기존 구조
	 * 유지를 위해 남겨둡니다.
	 * 
	 * @param consultNo   상담번호
	 * @param writerType  작성자 구분 (CUSTOMER / ADMIN / 고객 / 관리자 등)
	 * @param memoContent 새로 추가할 메모 내용
	 */
	public void appendConsultMemo(Integer consultNo, String writerType, String memoContent) {

		if (consultNo == null) {
			return;
		}

		if (memoContent == null || memoContent.trim().isEmpty()) {
			return;
		}

		Optional<DealerConsult> optionalConsult = consultRepository.findById(consultNo);

		if (optionalConsult.isEmpty()) {
			return;
		}

		DealerConsult consult = optionalConsult.get();

		String newMemoBlock = buildMemoBlock(writerType, memoContent);
		String oldMemo = consult.getConsultMemo();

		if (oldMemo == null || oldMemo.trim().isEmpty()) {
			consult.setConsultMemo(newMemoBlock);
		} else {
			consult.setConsultMemo(oldMemo + "\n\n" + newMemoBlock);
		}

		consultRepository.save(consult);
	}

	/**
	 * 상담 메모 누적 저장 (딜러번호 검증 포함 버전)
	 * 
	 * @param consultNo   상담번호
	 * @param dealerNo    딜러번호
	 * @param writerType  작성자 구분
	 * @param memoContent 새 메모 내용
	 */
	public void appendConsultMemo(Integer consultNo, Integer dealerNo, String writerType, String memoContent) {

		if (consultNo == null || dealerNo == null) {
			return;
		}

		if (memoContent == null || memoContent.trim().isEmpty()) {
			return;
		}

		Optional<DealerConsult> optionalConsult = consultRepository.findByConsultNoAndDealerNo(consultNo, dealerNo);

		if (optionalConsult.isEmpty()) {
			return;
		}

		DealerConsult consult = optionalConsult.get();

		String newMemoBlock = buildMemoBlock(writerType, memoContent);
		String oldMemo = consult.getConsultMemo();

		if (oldMemo == null || oldMemo.trim().isEmpty()) {
			consult.setConsultMemo(newMemoBlock);
		} else {
			consult.setConsultMemo(oldMemo + "\n\n" + newMemoBlock);
		}

		consultRepository.save(consult);
	}

	/**
	 * 대시보드용 전체 상담 수
	 */
	public long getTotalConsultCount(Integer dealerNo) {

		if (dealerNo == null) {
			return 0;
		}

		return consultRepository.countByDealerNo(dealerNo);
	}

	/**
	 * 대시보드용 대기 상담 수 기존 한글 데이터와 영문 데이터를 모두 합산합니다.
	 */
	public long getWaitingConsultCount(Integer dealerNo) {

		if (dealerNo == null) {
			return 0;
		}

		return consultRepository.countByDealerNoAndConsultStatus(dealerNo, "WAITING")
				+ consultRepository.countByDealerNoAndConsultStatus(dealerNo, "대기");
	}

	/**
	 * 대시보드용 진행중 상담 수 기존 한글 데이터와 영문 데이터를 모두 합산합니다.
	 */
	public long getInProgressConsultCount(Integer dealerNo) {

		if (dealerNo == null) {
			return 0;
		}

		return consultRepository.countByDealerNoAndConsultStatus(dealerNo, "IN_PROGRESS")
				+ consultRepository.countByDealerNoAndConsultStatus(dealerNo, "진행중");
	}

	/**
	 * 대시보드용 완료 상담 수 기존 한글 데이터와 영문 데이터를 모두 합산합니다.
	 */
	public long getCompletedConsultCount(Integer dealerNo) {

		if (dealerNo == null) {
			return 0;
		}

		return consultRepository.countByDealerNoAndConsultStatus(dealerNo, "COMPLETED")
				+ consultRepository.countByDealerNoAndConsultStatus(dealerNo, "완료");
	}

	/**
	 * 상태값을 영문 코드로 정규화
	 * 
	 * 허용 입력 - 대기 / WAITING - 진행중 / IN_PROGRESS - 완료 / COMPLETED
	 * 
	 * @param consultStatus 입력 상태값
	 * @return 영문 상태코드, 지원하지 않는 값이면 null
	 */
	private String normalizeStatus(String consultStatus) {

		if (consultStatus == null) {
			return null;
		}

		switch (consultStatus.trim()) {
		case "대기":
		case "WAITING":
			return "WAITING";

		case "진행중":
		case "IN_PROGRESS":
			return "IN_PROGRESS";

		case "완료":
		case "COMPLETED":
			return "COMPLETED";

		default:
			return null;
		}
	}

	/**
	 * 누적 저장용 메모 블록 생성
	 * 
	 * @param writerType  작성자 구분
	 * @param memoContent 메모 내용
	 * @return 누적 저장용 문자열 블록
	 */
	private String buildMemoBlock(String writerType, String memoContent) {

		String writerLabel = "기타";

		if (writerType != null) {
			String normalizedWriterType = writerType.trim().toUpperCase();

			if ("CUSTOMER".equals(normalizedWriterType) || "고객".equals(writerType.trim())) {
				writerLabel = "고객";
			} else if ("ADMIN".equals(normalizedWriterType) || "관리자".equals(writerType.trim())) {
				writerLabel = "관리자";
			} else if ("DEALER".equals(normalizedWriterType) || "딜러".equals(writerType.trim())) {
				writerLabel = "딜러";
			}
		}

		String nowText = LocalDateTime.now().format(MEMO_DATE_FORMATTER);
		String cleanedMemoContent = memoContent.trim();

		return "[" + writerLabel + "] " + nowText + "\n" + cleanedMemoContent;
	}
}