package com.kiaev.admin.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.kiaev.admin.consult.AdminConsultView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDashboardSummary {

    private long memberCount;
    private long carCount;
    private long consultCount;
    private long boardCount;
    private long dealerCount;
    private long promotionCount;
    private long waitingConsultCount;
    private long inProgressConsultCount;
    private long completedConsultCount;
    private long noticeCount;
    private long inquiryCount;
    private long waitingDealerCount;
    private long approvedDealerCount;
    private long rejectedDealerCount;
    private List<AdminConsultView> recentConsults = new ArrayList<>();
}
