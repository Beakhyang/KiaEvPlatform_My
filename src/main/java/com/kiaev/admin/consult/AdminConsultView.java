package com.kiaev.admin.consult;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminConsultView {

    private Long consultNo;
    private Long memberNo;
    private String memberName;
    private String memberEmail;
    private String memberPhone;
    private Long carNo;
    private Long carModelNo;
    private String carModelName;
    private Integer dealerNo;
    private String dealerName;
    private String dealerEmpNo;
    private Integer budgetAmount;
    private String usePurpose;
    private String mainRangeKm;
    private String fellowData;
    private String consultContent;
    private String consultStatus;
    private LocalDateTime requestDate;
    private LocalDateTime assignedDate;
    private LocalDateTime completedDate;
    private String consultMemo;

    public String getStatusLabel() {
        return switch (normalizeStatus(consultStatus)) {
        case "WAITING" -> "대기";
        case "IN_PROGRESS" -> "진행중";
        case "COMPLETED" -> "완료";
        default -> consultStatus == null ? "-" : consultStatus;
        };
    }

    public boolean isWaiting() {
        return "WAITING".equals(normalizeStatus(consultStatus));
    }

    private String normalizeStatus(String value) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        if ("대기".equals(trimmed) || "WAITING".equalsIgnoreCase(trimmed)) {
            return "WAITING";
        }
        if ("진행중".equals(trimmed) || "IN_PROGRESS".equalsIgnoreCase(trimmed)) {
            return "IN_PROGRESS";
        }
        if ("완료".equals(trimmed) || "COMPLETED".equalsIgnoreCase(trimmed)) {
            return "COMPLETED";
        }
        return trimmed;
    }
}
