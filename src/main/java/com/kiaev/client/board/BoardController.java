package com.kiaev.client.board;

import com.kiaev.client.login.Login;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public String boardList(@PageableDefault(size = 5) Pageable pageable, Model model) {
        Page<Board> boardPage = boardService.getVisibleBoardList(pageable);
        model.addAttribute("boardList", boardPage.getContent());
        model.addAttribute("boardPage", boardPage);
        return "client/board/boardList";
    }

    @GetMapping("/detail")
    public String boardDetail(@RequestParam("boardNo") Long boardNo, Model model, HttpSession session) {
        Board board = boardService.getBoardDetail(boardNo);
        Login loginUser = (Login) session.getAttribute("loginUser");
        boolean isAuthor = loginUser != null && boardService.isAuthor(board, loginUser.getMemberNo());

        model.addAttribute("board", board);
        model.addAttribute("isAuthor", isAuthor);
        return "client/board/boardDetail";
    }

    @GetMapping("/write")
    public String boardWriteForm(HttpSession session) {
        Login loginUser = (Login) session.getAttribute("loginUser");
        if (loginUser == null) {
            session.setAttribute("prevPage", "/board/write");
            return "redirect:/login";
        }
        return "client/board/boardWrite";
    }

    @PostMapping("/write")
    public String boardWriteProcess(@ModelAttribute Board board, HttpSession session) {
        Login loginUser = (Login) session.getAttribute("loginUser");
        if (loginUser == null) {
            session.setAttribute("prevPage", "/board/write");
            return "redirect:/login";
        }

        board.setMemberNo(loginUser.getMemberNo());
        boardService.saveBoard(board);
        return "redirect:/board/list";
    }

    @GetMapping("/edit")
    public String boardEditForm(@RequestParam("boardNo") Long boardNo, Model model, HttpSession session) {
        Login loginUser = (Login) session.getAttribute("loginUser");
        if (loginUser == null) {
            session.setAttribute("prevPage", "/board/edit?boardNo=" + boardNo);
            return "redirect:/login";
        }

        Board board = boardService.getBoardDetail(boardNo);
        if (!boardService.isAuthor(board, loginUser.getMemberNo())) {
            return "redirect:/board/detail?boardNo=" + boardNo;
        }

        model.addAttribute("board", board);
        return "client/board/boardEdit";
    }

    @PostMapping("/update")
    public String boardUpdateProcess(@ModelAttribute Board board, HttpSession session) {
        Login loginUser = (Login) session.getAttribute("loginUser");
        if (loginUser == null) {
            session.setAttribute("prevPage", "/board/edit?boardNo=" + board.getBoardNo());
            return "redirect:/login";
        }

        board.setMemberNo(loginUser.getMemberNo());
        boardService.updateBoard(board, loginUser.getMemberNo());
        return "redirect:/board/detail?boardNo=" + board.getBoardNo();
    }

    @PostMapping("/delete")
    public String boardDelete(@RequestParam("boardNo") Long boardNo, HttpSession session) {
        Login loginUser = (Login) session.getAttribute("loginUser");
        if (loginUser == null) {
            session.setAttribute("prevPage", "/board/detail?boardNo=" + boardNo);
            return "redirect:/login";
        }

        boardService.deleteBoard(boardNo, loginUser.getMemberNo());
        return "redirect:/board/list";
    }
}
