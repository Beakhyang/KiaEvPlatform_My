package com.kiaev.client.promotion;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PROMOTION_TBL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long id;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "sub_title", length = 255)
    private String subTitle;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "target_model", length = 50)
    private String targetModel;

    @Column(name = "discount_amount")
    private Integer discountAmount;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "banner_order")
    private Integer bannerOrder;

    @Column(name = "banner_image_url", length = 255)
    private String bannerImageUrl;

    @Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return (now.isAfter(this.startDate) || now.isEqual(this.startDate))
                && (now.isBefore(this.endDate) || now.isEqual(this.endDate));
    }

    public long getRemainDays() {
        if (this.endDate == null) {
            return 999;
        }

        return java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.now(),
                this.endDate.toLocalDate());
    }
}
