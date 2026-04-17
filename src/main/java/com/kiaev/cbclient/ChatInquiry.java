package com.kiaev.cbclient;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

// 반드시 jakarta.persistence 패키지를 사용해야 합니다 (Spring Boot 3 기준)
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_inquiry_tbl")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_no")
    private Long inquiry_no;       // 문의번호 (PK)

    @Column(name = "member_no")
    private Long member_no;        // 회원번호 (FK)

    @Column(name = "car_no")
    private Long car_no;           // 차량번호 (FK)

    @Column(name = "category", nullable = false, length = 50)
    private String category;       // 문의유형

    @Column(name = "writer_name", nullable = false, length = 50)
    private String writer_name;    // 작성자명

    @Column(name = "writer_email", nullable = false, length = 100)
    private String writer_email;   // 작성자이메일

    @Lob
    @Column(name = "content", nullable = false)
    private String content;        // 문의내용

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at; // 등록일시

    @Builder.Default
    @Column(name = "is_answered", length = 1)
    private String is_answered = "N"; // 답변여부

    @Column(name = "admin_no")
    private Long admin_no;         // 담당관리자
    @Lob
    @Column(name = "answer_content")
    private String answer_content;

    @Column(name = "answered_at")
    private LocalDateTime answered_at;

    @Column(name = "chat_source", length = 30)
    private String chat_source;

    @Column(name = "question_summary", length = 255)
    private String question_summary;

    @Column(name = "status", length = 20)
    private String status;
}
