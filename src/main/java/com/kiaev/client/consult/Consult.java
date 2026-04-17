package com.kiaev.client.consult;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "consult_tbl")
public class Consult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consult_no")
    private Long consultNo;

    @Column(name = "member_no", nullable = false)
    private Long memberNo;

    @Column(name = "car_no", nullable = false)
    private Long carNo;

    @Column(name = "dealer_no")
    private Long dealerNo;

    @Column(name = "budget_amount")
    private Integer budgetAmount;

    @Column(name = "use_purpose", length = 100)
    private String usePurpose;

    @Column(name = "main_range_km", nullable = false, length = 20)
    private String mainRangeKm;

    @Column(name = "fellow_data", length = 20)
    private String fellowData;

    @Column(name = "consult_content", length = 1000)
    private String consultContent;

    @Column(name = "consult_status", nullable = false, length = 20)
    private String consultStatus;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "consult_memo", length = 1000)
    private String consultMemo;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (this.requestDate == null) {
            this.requestDate = now;
        }

        if (this.consultStatus == null || this.consultStatus.isBlank()) {
            this.consultStatus = "대기";
        }

        if (this.updatedDate == null) {
            this.updatedDate = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}