package com.kiaev.dealer.sales;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kiaev.dealer.dealerconsult.DealerConsult;
import com.kiaev.dealer.dealerconsult.DealerConsultRepository;
import com.kiaev.dealer.dealerconsult.DealerConsultService;
import com.kiaev.dealer.dealerconsult.DealerConsultView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesService {

    public static final String REGISTER_SUCCESS_MESSAGE = "판매 등록이 완료되었습니다.";
    public static final String UPDATE_SUCCESS_MESSAGE = "판매 금액이 수정되었습니다.";

    private final SalesRepository salesRepository;
    private final DealerConsultRepository consultRepository;
    private final DealerConsultService dealerConsultService;

    public List<SalesView> getSalesListByDealerNo(Integer dealerNo) {
        if (dealerNo == null) {
            return new ArrayList<>();
        }

        List<Object[]> rows = salesRepository.findSalesListRowsByDealerNo(dealerNo);
        List<SalesView> salesList = new ArrayList<>();

        for (Object[] row : rows) {
            SalesView salesView = mapSalesRow(row);
            if (salesView != null) {
                salesList.add(salesView);
            }
        }

        return salesList;
    }

    public SalesView getSalesDetailByDealerNo(Integer salesNo, Integer dealerNo) {
        if (salesNo == null || dealerNo == null) {
            return null;
        }

        List<Object[]> rows = salesRepository.findSalesDetailRowsBySalesNoAndDealerNo(salesNo, dealerNo);
        if (rows == null || rows.isEmpty()) {
            return null;
        }

        return mapSalesRow(rows.get(0));
    }

    public List<DealerConsultView> getCompletedConsultList(Integer dealerNo) {
        if (dealerNo == null) {
            return new ArrayList<>();
        }

        List<DealerConsultView> consultList = dealerConsultService.getConsultList(dealerNo);
        List<DealerConsultView> availableConsultList = new ArrayList<>();

        for (DealerConsultView consult : consultList) {
            if (consult == null || consult.getConsultNo() == null) {
                continue;
            }

            if (!consult.isCompletedStatus()) {
                continue;
            }

            if (salesRepository.existsByConsultNo(consult.getConsultNo())) {
                continue;
            }

            availableConsultList.add(consult);
        }

        return availableConsultList;
    }

    public DealerConsultView getCompletedConsultDetail(Integer consultNo, Integer dealerNo) {
        if (consultNo == null || dealerNo == null) {
            return null;
        }

        DealerConsultView consultView = dealerConsultService.getConsultDetail(consultNo, dealerNo);
        if (consultView == null || !consultView.isCompletedStatus()) {
            return null;
        }

        if (salesRepository.existsByConsultNo(consultNo)) {
            return null;
        }

        return consultView;
    }

    public Sales createSalesFromConsult(Integer consultNo, Integer dealerNo) {
        if (consultNo == null || dealerNo == null) {
            return null;
        }

        Optional<DealerConsult> optionalConsult = consultRepository.findByConsultNoAndDealerNo(consultNo, dealerNo);
        if (optionalConsult.isEmpty()) {
            return null;
        }

        DealerConsult consult = optionalConsult.get();
        if (!isCompletedStatus(consult.getConsultStatus())) {
            return null;
        }

        if (salesRepository.existsByConsultNo(consultNo)) {
            return null;
        }

        Sales sales = new Sales();
        sales.setConsultNo(consult.getConsultNo());
        sales.setMemberNo(consult.getMemberNo());
        sales.setCarNo(consult.getCarNo());
        sales.setCarModelNo(consult.getCarModelNo());
        sales.setDealerNo(consult.getDealerNo());
        sales.setModelName(resolveCarModelName(consultNo, dealerNo));
        sales.setSalesDate(LocalDateTime.now());
        sales.setSalesStatus("COMPLETED");

        return sales;
    }

    @Transactional
    public String registerSales(Integer consultNo, Integer dealerNo, Integer salesAmount) {
        if (consultNo == null) {
            return "상담번호가 없습니다.";
        }

        if (dealerNo == null) {
            return "로그인 정보가 없습니다.";
        }

        if (salesAmount == null || salesAmount <= 0) {
            return "판매금액을 올바르게 입력해주세요.";
        }

        Optional<DealerConsult> optionalConsult = consultRepository.findByConsultNoAndDealerNo(consultNo, dealerNo);
        if (optionalConsult.isEmpty()) {
            return "해당 상담 정보를 찾을 수 없습니다.";
        }

        DealerConsult consult = optionalConsult.get();
        if (!isCompletedStatus(consult.getConsultStatus())) {
            return "완료 상태의 상담만 판매 등록할 수 있습니다.";
        }

        if (salesRepository.existsByConsultNo(consultNo)) {
            return "이미 판매 등록이 완료된 상담입니다.";
        }

        Sales sales = new Sales();
        sales.setConsultNo(consult.getConsultNo());
        sales.setCarNo(consult.getCarNo());
        sales.setCarModelNo(consult.getCarModelNo());
        sales.setModelName(resolveCarModelName(consultNo, dealerNo));
        sales.setMemberNo(consult.getMemberNo());
        sales.setDealerNo(consult.getDealerNo());
        sales.setSalesAmount(salesAmount);
        sales.setSalesDate(LocalDateTime.now());
        sales.setSalesStatus("COMPLETED");

        salesRepository.save(sales);

        consult.setConsultStatus("COMPLETED");
        if (consult.getAssignedDate() == null) {
            consult.setAssignedDate(LocalDateTime.now());
        }
        if (consult.getCompletedDate() == null) {
            consult.setCompletedDate(LocalDateTime.now());
        }

        consultRepository.save(consult);
        return REGISTER_SUCCESS_MESSAGE;
    }

    @Transactional
    public String updateSalesAmount(Integer salesNo, Integer dealerNo, Integer salesAmount) {
        if (salesNo == null) {
            return "판매번호가 없습니다.";
        }

        if (dealerNo == null) {
            return "로그인 정보가 없습니다.";
        }

        if (salesAmount == null || salesAmount <= 0) {
            return "판매금액을 올바르게 입력해주세요.";
        }

        Optional<Sales> optionalSales = salesRepository.findBySalesNoAndDealerNo(salesNo, dealerNo);
        if (optionalSales.isEmpty()) {
            return "수정할 판매 정보를 찾을 수 없습니다.";
        }

        Sales sales = optionalSales.get();
        sales.setSalesAmount(salesAmount);
        salesRepository.save(sales);
        return UPDATE_SUCCESS_MESSAGE;
    }

    public long getSalesCountByDealerNo(Integer dealerNo) {
        if (dealerNo == null) {
            return 0;
        }

        return salesRepository.countByDealerNo(dealerNo);
    }

    public Long getSalesAmountSumByDealerNo(Integer dealerNo) {
        if (dealerNo == null) {
            return 0L;
        }

        Long totalAmount = salesRepository.sumSalesAmountByDealerNo(dealerNo);
        return totalAmount != null ? totalAmount : 0L;
    }

    private SalesView mapSalesRow(Object[] row) {
        if (row == null || row.length < 15) {
            return null;
        }

        SalesView salesView = new SalesView();
        salesView.setSalesNo(toInteger(row[0]));
        salesView.setConsultNo(toInteger(row[1]));
        salesView.setMemberNo(toInteger(row[2]));
        salesView.setDealerNo(toInteger(row[3]));
        salesView.setCustomerName(toStringValue(row[4]));
        salesView.setCustomerPhone(toStringValue(row[5]));
        salesView.setCustomerEmail(toStringValue(row[6]));
        salesView.setCarIdentifier(toStringValue(row[7]));
        salesView.setCarModelName(toStringValue(row[8]));
        salesView.setSalesAmount(toInteger(row[9]));
        salesView.setSalesDate(toLocalDateTime(row[10]));
        salesView.setCreatedAt(toLocalDateTime(row[11]));
        salesView.setSalesStatus(toStringValue(row[12]));
        salesView.setConsultRequestDate(toLocalDateTime(row[13]));
        salesView.setConsultCompletedDate(toLocalDateTime(row[14]));
        return salesView;
    }

    private String resolveCarModelName(Integer consultNo, Integer dealerNo) {
        DealerConsultView consultView = dealerConsultService.getConsultDetail(consultNo, dealerNo);
        if (consultView == null || isBlank(consultView.getCarModelName())) {
            return null;
        }

        return consultView.getCarModelName().trim();
    }

    private boolean isCompletedStatus(String consultStatus) {
        if (consultStatus == null) {
            return false;
        }

        String trimmedStatus = consultStatus.trim();
        return "COMPLETED".equals(trimmedStatus) || "완료".equals(trimmedStatus);
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String toStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }

        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
