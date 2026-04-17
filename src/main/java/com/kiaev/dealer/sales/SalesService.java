package com.kiaev.dealer.sales;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kiaev.dealer.dealerconsult.DealerConsult;
import com.kiaev.dealer.dealerconsult.DealerConsultRepository;

/**
 * 판매 관리 Service
 * 
 * 기능 - 전체 판매 목록 조회 - 로그인한 딜러 판매 목록 조회 - 판매 상세 조회 - 판매 등록 가능한 상담 목록 조회 - 상담 선택 후
 * 판매 등록용 객체 생성 - 판매 등록 처리 - 딜러별 판매 건수 / 총액 조회
 * 
 * 중요 - 기존 기능은 유지 - 현재 실제 DealerConsult.java 구조에 맞춰 컴파일 오류가 나지 않도록 유지 -
 * consult.getCarNo() 는 String 타입으로 처리 - consult.getModelName() 은 현재 엔티티에 없을 수
 * 있으므로 여기서는 사용하지 않음 - carModelNo 는 consult.getCarModelNo() 로 사용 - created_at 은
 * Sales 엔티티에서 DB 자동 생성 처리
 * 
 * 이번 수정 사항 - 판매 등록 화면에서는 완료 상태 상담만 선택 가능하도록 제한 - 진행중 / 배정완료 상담은 판매 등록 목록에서 제외
 */
@Service
public class SalesService {

	@Autowired
	private SalesRepository salesRepository;

	@Autowired
	private DealerConsultRepository consultRepository;

	/**
	 * 전체 판매 목록 조회
	 */
	public List<Sales> getSalesList() {
		return salesRepository.findAll();
	}

	/**
	 * 로그인한 딜러 판매 목록 조회
	 */
	public List<Sales> getSalesListByDealerNo(Integer dealerNo) {

		if (dealerNo == null) {
			return new ArrayList<>();
		}

		return salesRepository.findByDealerNoOrderBySalesNoDesc(dealerNo);
	}

	/**
	 * 판매 상세 조회
	 */
	public Sales getSalesDetail(Integer salesNo) {

		if (salesNo == null) {
			return null;
		}

		return salesRepository.findById(salesNo).orElse(null);
	}

	/**
	 * 로그인한 딜러 본인 판매 상세 조회
	 */
	public Sales getSalesDetailByDealerNo(Integer salesNo, Integer dealerNo) {

		if (salesNo == null || dealerNo == null) {
			return null;
		}

		Optional<Sales> optionalSales = salesRepository.findBySalesNoAndDealerNo(salesNo, dealerNo);
		return optionalSales.orElse(null);
	}

	/**
	 * 판매 등록 가능한 상담 목록 조회
	 * 
	 * 조건 - 로그인한 딜러의 상담만 조회 - 상담 상태가 완료 / COMPLETED 인 상담만 조회 - 이미 판매 등록된 상담 제외 - 이미
	 * 판매된 차량 제외
	 */
	public List<DealerConsult> getCompletedConsultList(Integer dealerNo) {

		if (dealerNo == null) {
			return new ArrayList<>();
		}

		// =====================================================
		// 완료 상태 상담만 1차 조회
		// - 상태값이 한글/영문 혼용될 수 있으므로 둘 다 조회
		// =====================================================
		List<DealerConsult> consultList = consultRepository
				.findByDealerNoAndConsultStatusInOrderByConsultNoAsc(dealerNo, Arrays.asList("COMPLETED", "완료"));

		List<DealerConsult> availableConsultList = new ArrayList<>();

		for (DealerConsult consult : consultList) {

			if (consult == null) {
				continue;
			}

			// 완료 상태가 아니면 제외
			if (!isCompletedStatus(consult.getConsultStatus())) {
				continue;
			}

			// 상담번호가 없으면 제외
			if (consult.getConsultNo() == null) {
				continue;
			}

			// 이미 판매 등록된 상담이면 제외
			if (salesRepository.existsByConsultNo(consult.getConsultNo())) {
				continue;
			}

			// 차량번호가 없으면 제외
			if (consult.getCarNo() == null || consult.getCarNo().trim().isEmpty()) {
				continue;
			}

			String carNo = consult.getCarNo();

			// 이미 판매된 차량이면 제외
			if (salesRepository.existsByCarNo(carNo)) {
				continue;
			}

			availableConsultList.add(consult);
		}

		return availableConsultList;
	}

	/**
	 * 상담 선택 후 판매 등록 폼용 Sales 객체 생성
	 * 
	 * 완료 상태 상담만 선택 가능
	 */
	public Sales createSalesFromConsult(Integer consultNo, Integer dealerNo) {

		if (consultNo == null || dealerNo == null) {
			return null;
		}

		Optional<DealerConsult> optionalConsult = consultRepository.findByConsultNoAndDealerNo(consultNo, dealerNo);

		if (optionalConsult.isEmpty()) {
			return null;
		}

		DealerConsult consult = optionalConsult.get();

		// 완료 상태 상담만 허용
		if (!isCompletedStatus(consult.getConsultStatus())) {
			return null;
		}

		// 이미 판매 등록된 상담이면 불가
		if (salesRepository.existsByConsultNo(consultNo)) {
			return null;
		}

		// 차량번호가 없으면 불가
		if (consult.getCarNo() == null || consult.getCarNo().trim().isEmpty()) {
			return null;
		}

		String carNo = consult.getCarNo();

		// 이미 판매된 차량이면 불가
		if (salesRepository.existsByCarNo(carNo)) {
			return null;
		}

		// 판매 등록 폼에 미리 채워줄 객체 생성
		Sales sales = new Sales();
		sales.setConsultNo(consult.getConsultNo());
		sales.setMemberNo(consult.getMemberNo());
		sales.setCarNo(consult.getCarNo());
		sales.setCarModelNo(consult.getCarModelNo());
		sales.setDealerNo(consult.getDealerNo());
		sales.setSalesDate(LocalDateTime.now());
		sales.setSalesStatus("COMPLETED");

		return sales;
	}

	/**
	 * 판매 등록 처리
	 * 
	 * 처리 순서 - 상담번호 / 로그인정보 / 판매금액 검증 - 상담 조회 - 완료 상태인지 확인 - 중복 판매 여부 확인 - 판매 저장 -
	 * 상담 자동 완료 처리
	 */
	@Transactional
	public String registerSales(Integer consultNo, Integer dealerNo, Integer salesAmount) {

		// 상담번호 체크
		if (consultNo == null) {
			return "상담번호가 없습니다.";
		}

		// 로그인 딜러 체크
		if (dealerNo == null) {
			return "로그인 정보가 없습니다.";
		}

		// 판매금액 체크
		if (salesAmount == null || salesAmount <= 0) {
			return "판매금액을 올바르게 입력해주세요.";
		}

		// 상담 조회
		Optional<DealerConsult> optionalConsult = consultRepository.findByConsultNoAndDealerNo(consultNo, dealerNo);

		if (optionalConsult.isEmpty()) {
			return "해당 상담 정보를 찾을 수 없습니다.";
		}

		DealerConsult consult = optionalConsult.get();

		// 완료 상태 상담만 판매 등록 가능
		if (!isCompletedStatus(consult.getConsultStatus())) {
			return "완료 상태의 상담만 판매 등록할 수 있습니다.";
		}

		// 이미 판매 등록된 상담인지 확인
		if (salesRepository.existsByConsultNo(consultNo)) {
			return "이미 판매 등록이 완료된 상담입니다.";
		}

		// 차량번호 체크
		if (consult.getCarNo() == null || consult.getCarNo().trim().isEmpty()) {
			return "차량번호가 없는 상담은 판매 등록할 수 없습니다.";
		}

		String carNo = consult.getCarNo();

		// 같은 차량번호 중복 판매 방지
		if (salesRepository.existsByCarNo(carNo)) {
			return "이미 판매 완료된 차량입니다.";
		}

		// 판매 엔티티 생성
		Sales sales = new Sales();
		sales.setConsultNo(consult.getConsultNo());
		sales.setCarNo(consult.getCarNo());
		sales.setCarModelNo(consult.getCarModelNo());
		sales.setMemberNo(consult.getMemberNo());
		sales.setDealerNo(consult.getDealerNo());
		sales.setSalesAmount(salesAmount);
		sales.setSalesDate(LocalDateTime.now());
		sales.setSalesStatus("COMPLETED");

		// 판매 저장
		salesRepository.save(sales);

		// 상담 자동 완료 처리
		// 이미 완료 상태더라도 현재 흐름 유지 차원에서 다시 완료 저장 가능
		consult.setConsultStatus("완료");

		// 배정일시가 없으면 현재 시간 저장
		if (consult.getAssignedDate() == null) {
			consult.setAssignedDate(LocalDateTime.now());
		}

		// 완료일시 저장
		consult.setCompletedDate(LocalDateTime.now());

		// 상담 저장
		consultRepository.save(consult);

		return "판매 등록이 완료되었습니다.";
	}

	/**
	 * 딜러 기준 판매 건수 조회
	 */
	public long getSalesCountByDealerNo(Integer dealerNo) {

		if (dealerNo == null) {
			return 0;
		}

		return salesRepository.countByDealerNo(dealerNo);
	}

	/**
	 * 딜러 기준 판매 총액 조회
	 */
	public Long getSalesAmountSumByDealerNo(Integer dealerNo) {

		if (dealerNo == null) {
			return 0L;
		}

		Long totalAmount = salesRepository.sumSalesAmountByDealerNo(dealerNo);
		return totalAmount != null ? totalAmount : 0L;
	}

	/**
	 * 상담 상태가 완료인지 판별
	 * 
	 * 허용 상태 - 완료 - COMPLETED
	 */
	private boolean isCompletedStatus(String consultStatus) {
		return "완료".equals(consultStatus) || "COMPLETED".equals(consultStatus);
	}
}