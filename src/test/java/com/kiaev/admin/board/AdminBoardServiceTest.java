package com.kiaev.admin.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.kiaev.admin.login.Admin;
import com.kiaev.client.board.Board;
import com.kiaev.client.board.BoardRepository;

class AdminBoardServiceTest {

    private final BoardRepository boardRepository = Mockito.mock(BoardRepository.class);
    private final AdminBoardService adminBoardService = new AdminBoardService(boardRepository);

    @Test
    void saveNoticeNormalizesCheckboxValuesBeforeSaving() {
        Admin admin = new Admin();
        admin.setAdminNo(99L);

        Board request = new Board();
        request.setTitle("공지 제목");
        request.setContent("공지 내용");
        request.setPriority(3);
        request.setIsPinned("N,Y");
        request.setHidden("N");

        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adminBoardService.saveNotice(request, admin);

        ArgumentCaptor<Board> captor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(captor.capture());

        Board saved = captor.getValue();
        assertEquals("NOTICE", saved.getBoardType());
        assertEquals("Y", saved.getNoticeYn());
        assertEquals("Y", saved.getIsPinned());
        assertEquals("N", saved.getHidden());
        assertEquals("N", saved.getDeletedYn());
        assertEquals(3, saved.getPriority());
        assertEquals(99L, saved.getAdminNo());
        assertNull(saved.getMemberNo());
    }

    @Test
    void saveNoticeDefaultsBlankCheckboxValuesToN() {
        Admin admin = new Admin();
        admin.setAdminNo(7L);

        Board existing = new Board();
        existing.setBoardNo(1L);
        existing.setHidden("Y");
        existing.setIsPinned("Y");

        Board request = new Board();
        request.setBoardNo(1L);
        request.setTitle("수정 제목");
        request.setContent("수정 내용");
        request.setIsPinned(null);
        request.setHidden("");

        when(boardRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adminBoardService.saveNotice(request, admin);

        ArgumentCaptor<Board> captor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(captor.capture());

        Board saved = captor.getValue();
        assertEquals("N", saved.getIsPinned());
        assertEquals("N", saved.getHidden());
        assertEquals("NOTICE", saved.getBoardType());
        assertEquals("Y", saved.getNoticeYn());
    }
}
