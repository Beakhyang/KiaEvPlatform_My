package com.kiaev.dealer.dealerconsult;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 딜러 상담 Service
 *
 * 조회 화면은 고객/차량 조인 데이터가 필요하므로 뷰용 DTO를 만들어 내려주고,
 * 상태 변경/메모 저장은 기존 상담 엔티티를 그대로 사용한다.
 */
@Service
public class DealerConsultService {

	@Autowired
	private DealerConsultRepository consultRepository;

	private static final DateTimeFormatter MEMO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public List<DealerConsultView> getConsultList(Integer dealerNo) {

		if (dealerNo == null) {
			return new ArrayList<>();
		}

		List<Object[]> rows = consultRepository.findConsultListRowsByDealerNo(dealerNo);
		List<DealerConsultView> consultList = new ArrayList<>();

		for (Object[] row : rows) {
			DealerConsultView consultView = mapConsultRow(row);
			if (consultView != null) {
				consultList.add(consultView);
			}
		}

		return consultList;
	}

	public DealerConsultView getConsultDetail(Integer consultNo, Integer dealerNo) {

		if (consultNo == null || dealerNo == null) {
			return null;
		}

		List<Object[]> rows = consultRepository.findConsultDetailRowsByConsultNoAndDealerNo(consultNo, dealerNo);
		if (rows == null || rows.isEmpty()) {
			return null;
		}

		return mapConsultRow(rows.get(0));
	}

	public boolean updateConsultStatus(Integer consultNo, Integer dealerNo, String consultStatus) {

		if (consultNo == null || dealerNo == null || consultStatus == null) {
			return false;
		}

		Optional<DealerConsult> optionalConsult = consultRepository.findByConsultNoAndDealerNo(consultNo, dealerNo);
		if (optionalConsult.isEmpty()) {
			return false;
		}

		DealerConsult consult = optionalConsult.get();
		String normalizedStatus = normalizeStatus(consultStatus);
		if (normalizedStatus == null) {
			return false;
		}

		consult.setConsultStatus(normalizedStatus);

		if ("IN_PROGRESS".equals(normalizedStatus)) {
			if (consult.getAssignedDate() == null) {
				consult.setAssignedDate(LocalDateTime.now());
			}
			consult.setCompletedDate(null);
		} else if ("COMPLETED".equals(normalizedStatus)) {
			if (consult.getAssignedDate() == null) {
				consult.setAssignedDate(LocalDateTime.now());
			}
			consult.setCompletedDate(LocalDateTime.now());
		} else if ("WAITING".equals(normalizedStatus)) {
			consult.setCompletedDate(null);
		}

		consultRepository.save(consult);
		return true;
	}

	public void appendConsultMemo(Integer consultNo, String writerType, String memoContent) {

		if (consultNo == null || memoContent == null || memoContent.trim().isEmpty()) {
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

	public void appendConsultMemo(Integer consultNo, Integer dealerNo, String writerType, String memoContent) {

		if (consultNo == null || dealerNo == null || memoContent == null || memoContent.trim().isEmpty()) {
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

	public long getTotalConsultCount(Integer dealerNo) {
		if (dealerNo == null) {
			return 0;
		}
		return consultRepository.countByDealerNo(dealerNo);
	}

	public long getWaitingConsultCount(Integer dealerNo) {
		if (dealerNo == null) {
			return 0;
		}
		return consultRepository.countByDealerNoAndConsultStatus(dealerNo, "WAITING")
				+ consultRepository.countByDealerNoAndConsultStatus(dealerNo, "대기")
				+ consultRepository.countByDealerNoAndConsultStatus(dealerNo, "신규 상담신청");
	}

	public long getInProgressConsultCount(Integer dealerNo) {
		if (dealerNo == null) {
			return 0;
		}
		return consultRepository.countByDealerNoAndConsultStatus(dealerNo, "IN_PROGRESS")
				+ consultRepository.countByDealerNoAndConsultStatus(dealerNo, "진행중");
	}

	public long getCompletedConsultCount(Integer dealerNo) {
		if (dealerNo == null) {
			return 0;
		}
		return consultRepository.countByDealerNoAndConsultStatus(dealerNo, "COMPLETED")
				+ consultRepository.countByDealerNoAndConsultStatus(dealerNo, "완료");
	}

	private DealerConsultView mapConsultRow(Object[] row) {

		if (row == null || row.length < 17) {
			return null;
		}

		DealerConsultView consultView = new DealerConsultView();

		consultView.setConsultNo(toInteger(row[0]));
		consultView.setMemberNo(toInteger(row[1]));
		consultView.setDealerNo(toInteger(row[2]));
		consultView.setBudgetAmount(toInteger(row[3]));
		consultView.setCustomerName(toStringValue(row[4]));
		consultView.setCustomerPhone(toStringValue(row[5]));
		consultView.setCustomerEmail(toStringValue(row[6]));
		consultView.setCarModelName(toStringValue(row[7]));
		consultView.setConsultContent(toStringValue(row[8]));
		consultView.setConsultMemo(toStringValue(row[9]));

		String rawStatus = toStringValue(row[10]);
		String normalizedStatus = normalizeStatus(rawStatus);
		consultView.setConsultStatus(normalizedStatus != null ? normalizedStatus : rawStatus);

		consultView.setUsePurpose(toStringValue(row[11]));
		consultView.setMainRangeKm(toStringValue(row[12]));
		consultView.setFellowData(toStringValue(row[13]));
		consultView.setRequestDate(toLocalDateTime(row[14]));
		consultView.setAssignedDate(toLocalDateTime(row[15]));
		consultView.setCompletedDate(toLocalDateTime(row[16]));

		return consultView;
	}

	private String normalizeStatus(String consultStatus) {

		if (consultStatus == null) {
			return null;
		}

		String trimmedStatus = consultStatus.trim();

		switch (trimmedStatus) {
		case "WAITING":
		case "대기":
		case "신규 상담신청":
			return "WAITING";

		case "IN_PROGRESS":
		case "진행중":
			return "IN_PROGRESS";

		case "COMPLETED":
		case "완료":
			return "COMPLETED";

		default:
			return null;
		}
	}

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
		return "[" + writerLabel + "] " + nowText + "\n" + memoContent.trim();
	}

	private Integer toInteger(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		try {
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String toStringValue(Object value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}

	private LocalDateTime toLocalDateTime(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof LocalDateTime) {
			return (LocalDateTime) value;
		}

		if (value instanceof Timestamp) {
			return ((Timestamp) value).toLocalDateTime();
		}

		return null;
	}
}
