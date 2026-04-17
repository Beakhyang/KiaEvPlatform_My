package com.kiaev.client.mypage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MypageRepository {

    private final JdbcTemplate jdbcTemplate;

    public MypageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mypage findMemberInfoByMemberNo(Long memberNo) {
        Mypage m = new Mypage();
        m.setMemberNo(memberNo);

        // 회원 고정값 제거
        m.setLoginId(null);
        m.setMemberName(null);
        m.setEmail(null);
        m.setPhone(null);
        m.setAddress(null);

        return m;
    }

    public void updateMemberInfo(Mypage m) {
        // DB UPDATE 자리
    }

    // 비밀번호 변경
    public boolean updatePassword(Long memberNo, String currentPw, String newPw) {

        // 예시: 현재 비밀번호가 1234일 때만 변경
        if ("1234".equals(currentPw)) {
            // 실제 DB에서는 UPDATE 쿼리 실행
            return true;
        }
        return false;
    }

    public List<Mypage> findConsultHistoryByMemberNo(Long memberNo) {
        // 상담내역 고정값 제거
        return new ArrayList<>();
    }

    public List<Mypage> findFavoriteCarsByMemberNo(Long memberNo) {
        return new ArrayList<>();
    }

    public List<Mypage> findBoardHistoryByMemberNo(Long memberNo) {

        String sql = """
            SELECT 
                board_no,
                title,
                content,
                created_at,
                inquiry_status
            FROM board_tbl
            WHERE member_no = ?
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mypage m = new Mypage();

            m.setBoardNo(rs.getLong("board_no"));
            m.setBoardTitle(rs.getString("title"));
            m.setBoardContent(rs.getString("content"));

            if (rs.getTimestamp("created_at") != null) {
                m.setBoardDate(
                    rs.getTimestamp("created_at")
                      .toLocalDateTime()
                      .toLocalDate()
                );
            }

            m.setBoardStatus(rs.getString("inquiry_status"));

            return m;
        }, memberNo);
    }

    public void deleteMember(Long memberNo) {
        // 실제 DB 삭제 또는 상태 변경 자리
        System.out.println("회원 탈퇴 처리 완료: " + memberNo);
    }

    public List<Wishlist> getWishlist(Long memberNo) {
        return new ArrayList<>();
    }

    public void deleteWishlist(Long wishlistNo, Long memberNo) {
        System.out.println("관심 차량 삭제");
    }

    public void toggleWishlist(Long memberNo, Long carId) {
        System.out.println("관심 차량 등록/취소");
        System.out.println("memberNo: " + memberNo);
        System.out.println("carId: " + carId);
    }
}