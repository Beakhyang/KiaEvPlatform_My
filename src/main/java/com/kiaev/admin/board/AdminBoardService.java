package com.kiaev.admin.board;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kiaev.admin.login.Admin;
import com.kiaev.client.board.Board;
import com.kiaev.client.board.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminBoardService {

    private final BoardRepository boardRepository;

    public List<Board> getBoards(String boardType) {
        return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "boardNo")).stream()
                .filter(board -> !"Y".equalsIgnoreCase(board.getDeletedYn()))
                .filter(board -> boardType == null || boardType.isBlank() || boardType.equalsIgnoreCase(board.getBoardType()))
                .toList();
    }

    public Board getBoard(Long boardNo) {
        return boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + boardNo));
    }

    public Board createNotice() {
        Board board = new Board();
        board.setBoardType("NOTICE");
        board.setNoticeYn("Y");
        board.setHidden("N");
        board.setDeletedYn("N");
        board.setIsPinned("N");
        board.setPriority(0);
        return board;
    }

    public void saveNotice(Board board, Admin admin) {
        Board target = board.getBoardNo() == null ? createNotice() : getBoard(board.getBoardNo());

        target.setBoardType("NOTICE");
        target.setNoticeYn("Y");
        target.setAdminNo(admin.getAdminNo());
        target.setMemberNo(null);
        target.setTitle(board.getTitle());
        target.setContent(board.getContent());
        target.setIsPinned(isBlank(board.getIsPinned()) ? "N" : board.getIsPinned());
        target.setHidden(isBlank(board.getHidden()) ? "N" : board.getHidden());
        target.setDeletedYn("N");
        target.setPriority(board.getPriority() == null ? 0 : board.getPriority());

        boardRepository.save(target);
    }

    public void answerInquiry(Long boardNo, String answerContent, Admin admin) {
        Board board = getBoard(boardNo);
        board.setAdminNo(admin.getAdminNo());
        board.setAnswerContent(answerContent);
        board.setAnswerDate(LocalDateTime.now());
        board.setInquiryStatus("COMPLETED");
        boardRepository.save(board);
    }

    public void deleteBoard(Long boardNo) {
        Board board = getBoard(boardNo);
        board.setDeletedYn("Y");
        boardRepository.save(board);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
