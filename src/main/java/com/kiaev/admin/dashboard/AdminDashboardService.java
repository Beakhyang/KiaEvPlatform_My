package com.kiaev.admin.dashboard;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kiaev.admin.consult.AdminConsultService;
import com.kiaev.admin.consult.AdminConsultView;
import com.kiaev.client.board.BoardRepository;
import com.kiaev.client.car.CarService;
import com.kiaev.client.consult.ConsultRepository;
import com.kiaev.client.member.MemberRepository;
import com.kiaev.client.promotion.PromotionRepository;
import com.kiaev.dealer.login.DealerLoginRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final MemberRepository memberRepository;
    private final CarService carService;
    private final ConsultRepository consultRepository;
    private final BoardRepository boardRepository;
    private final DealerLoginRepository dealerLoginRepository;
    private final PromotionRepository promotionRepository;
    private final AdminConsultService adminConsultService;

    public AdminDashboardSummary getSummary() {
        AdminDashboardSummary summary = new AdminDashboardSummary();
        List<AdminConsultView> consultViews = adminConsultService.getConsultList(null);

        summary.setMemberCount(memberRepository.findAll().stream()
                .filter(member -> !"탈퇴회원".equals(member.getMemberStatus()))
                .count());
        summary.setCarCount(carService.countVisibleCars());
        summary.setConsultCount(consultRepository.count());
        summary.setBoardCount(boardRepository.findAll().stream()
                .filter(board -> !"Y".equalsIgnoreCase(board.getDeletedYn()))
                .count());
        summary.setDealerCount(dealerLoginRepository.count());
        summary.setPromotionCount(promotionRepository.count());
        summary.setWaitingConsultCount(consultViews.stream().filter(AdminConsultView::isWaiting).count());
        summary.setInProgressConsultCount(consultViews.stream()
                .filter(view -> "진행중".equals(view.getStatusLabel()))
                .count());
        summary.setCompletedConsultCount(consultViews.stream()
                .filter(view -> "완료".equals(view.getStatusLabel()))
                .count());
        summary.setNoticeCount(boardRepository.findAll().stream()
                .filter(board -> !"Y".equalsIgnoreCase(board.getDeletedYn()))
                .filter(board -> "NOTICE".equalsIgnoreCase(board.getBoardType()))
                .count());
        summary.setInquiryCount(boardRepository.findAll().stream()
                .filter(board -> !"Y".equalsIgnoreCase(board.getDeletedYn()))
                .filter(board -> !"NOTICE".equalsIgnoreCase(board.getBoardType()))
                .count());
        summary.setWaitingDealerCount(dealerLoginRepository.findAll().stream()
                .filter(dealer -> "WAIT".equalsIgnoreCase(dealer.getApprovalStatus()))
                .count());
        summary.setApprovedDealerCount(dealerLoginRepository.findAll().stream()
                .filter(dealer -> "APPROVED".equalsIgnoreCase(dealer.getApprovalStatus()))
                .count());
        summary.setRejectedDealerCount(dealerLoginRepository.findAll().stream()
                .filter(dealer -> "REJECTED".equalsIgnoreCase(dealer.getApprovalStatus()))
                .count());
        summary.setRecentConsults(consultViews.stream().limit(5).toList());

        return summary;
    }
}
