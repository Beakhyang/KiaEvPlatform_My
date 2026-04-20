package com.kiaev.admin.dealer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kiaev.dealer.login.DealerLogin;
import com.kiaev.dealer.login.DealerLoginRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDealerService {

    private final DealerLoginRepository dealerLoginRepository;

    public List<DealerLogin> getDealers(String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        return dealerLoginRepository.findAll(Sort.by(Sort.Direction.DESC, "dealerNo")).stream()
                .filter(dealer -> normalizedKeyword.isBlank()
                        || contains(dealer.getDealerEmpNo(), normalizedKeyword)
                        || contains(dealer.getDealerName(), normalizedKeyword)
                        || contains(dealer.getAffiliation(), normalizedKeyword))
                .toList();
    }

    public DealerLogin getDealer(Integer dealerNo) {
        return dealerLoginRepository.findById(dealerNo)
                .orElseThrow(() -> new IllegalArgumentException("딜러를 찾을 수 없습니다. ID: " + dealerNo));
    }

    public DealerLogin createEmptyDealer() {
        DealerLogin dealer = new DealerLogin();
        dealer.setDealerStatus("ACTIVE");
        dealer.setApprovalStatus("WAIT");
        return dealer;
    }

    public void save(DealerLogin dealer) {
        if (dealer.getDealerNo() == null) {
            dealer.setDealerEmpNo(generateDealerEmpNo());
            if (dealer.getDealerPw() == null || dealer.getDealerPw().isBlank()) {
                dealer.setDealerPw("1234");
            }
            if (dealer.getDealerStatus() == null || dealer.getDealerStatus().isBlank()) {
                dealer.setDealerStatus("ACTIVE");
            }
            if (dealer.getApprovalStatus() == null || dealer.getApprovalStatus().isBlank()) {
                dealer.setApprovalStatus("WAIT");
            }
            dealerLoginRepository.save(dealer);
            return;
        }

        DealerLogin existing = getDealer(dealer.getDealerNo());
        existing.setDealerName(dealer.getDealerName());
        existing.setBirthDate(dealer.getBirthDate());
        existing.setPhone(dealer.getPhone());
        existing.setEmail(dealer.getEmail());
        existing.setAffiliation(dealer.getAffiliation());
        existing.setDealerStatus(isBlank(dealer.getDealerStatus()) ? existing.getDealerStatus() : dealer.getDealerStatus());
        existing.setApprovalStatus(isBlank(dealer.getApprovalStatus()) ? existing.getApprovalStatus() : dealer.getApprovalStatus());

        if (!isBlank(dealer.getDealerPw())) {
            existing.setDealerPw(dealer.getDealerPw());
        }

        dealerLoginRepository.save(existing);
    }

    public void updateApproval(Integer dealerNo, String approvalStatus) {
        DealerLogin dealer = getDealer(dealerNo);
        dealer.setApprovalStatus(approvalStatus);
        dealerLoginRepository.save(dealer);
    }

    public void delete(Integer dealerNo) {
        DealerLogin dealer = getDealer(dealerNo);
        dealer.setDealerStatus("INACTIVE");
        dealerLoginRepository.save(dealer);
    }

    public List<DealerLogin> getAssignableDealers() {
        return dealerLoginRepository.findAll().stream()
                .filter(dealer -> "ACTIVE".equalsIgnoreCase(dealer.getDealerStatus()))
                .filter(dealer -> "APPROVED".equalsIgnoreCase(dealer.getApprovalStatus()))
                .sorted(Comparator.comparing(DealerLogin::getDealerNo))
                .toList();
    }

    private String generateDealerEmpNo() {
        String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "D";

        int nextSequence = dealerLoginRepository.findAll().stream()
                .map(DealerLogin::getDealerEmpNo)
                .filter(value -> value != null && value.startsWith(prefix))
                .map(value -> value.substring(prefix.length()))
                .mapToInt(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0) + 1;

        return prefix + String.format("%03d", nextSequence);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
