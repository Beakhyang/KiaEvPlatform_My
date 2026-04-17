package com.kiaev.client.board;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "BOARD_TBL") // 명세서와 동일한 테이블명
@Getter
@Setter
@NoArgsConstructor // 기본 생성자를 자동으로 만들어줍니다. (JPA 필수)
@AllArgsConstructor // 모든 필드값을 파라미터로 받는 생성자를 만들어줍니다.
@Builder // 객체를 생성할 때 매우 직관적이고 편하게 만들 수 있는 빌더 패턴을 제공합니다.
public class Board {

	// 1. 게시글번호 (PK, 시퀀스/자동증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_no")
    private Long boardNo; 

    // 2. 게시글타입 (문의글/공지사항)
    @Column(name = "board_type", length = 20)
    private String boardType; 

    // 3. 회원번호 (FK)
    @Column(name = "member_no")
    private Long memberNo; 

    // 4. 관리자번호 (FK)
    @Column(name = "admin_no")
    private Long adminNo; 

    // 5. 제목 (Null 불가, 길이 2000)
    @Column(name = "title", nullable = false, length = 2000)
    private String title; 

    // 6. 내용 (Null 불가, 길이 2000)
    @Column(name = "content", nullable = false, length = 2000)
    private String content; 

    // 7. 상단고정여부 (Y/N)
    @Column(name = "is_pinned", length = 1)
    private String isPinned; 

    // 8. 문의상태 (답변대기/답변완료)
    @Column(name = "inquiry_status", length = 20)
    private String inquiryStatus; 

    // 9. 답변내용
    @Column(name = "answer_content", length = 2000)
    private String answerContent; 

    // 10. 답변일시
    @Column(name = "answer_date")
    private LocalDateTime answerDate; 

    // 11. 조회수 (Null 불가, 기본값 0)
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0; 

    // 12. 등록일시 (Null 불가, 자동입력)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; 

    // 13. 수정일시
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; 

    // 14. 삭제여부 (Null 불가, 기본값 N)
    @Column(name = "deleted_yn", nullable = false, length = 1)
    private String deletedYn = "N";
    
    // 15. 숨김여부 (Y/N)
    @Builder.Default // @Builder를 사용할 때 아래 초기값("N")이 무시되지 않도록 해줍니다.
    @Column(name = "hidden", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String hidden = "N"; // Java 객체 생성 시 기본값 'N'
    
    // 16. 공지사항 여부 (Y/N)
    @Column(name = "NOTICE_YN")
    private String noticeYn = "N"; 

    // 17. 우선순위 
    @Column(name = "PRIORITY")
    private Integer priority = 0; 
}