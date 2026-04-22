package com.kiaev.admin.board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kiaev.admin.login.Admin;
import com.kiaev.client.board.Board;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/board")
@RequiredArgsConstructor
public class AdminBoardController {

    private final AdminBoardService adminBoardService;

    @GetMapping("/list")
    public String list(@RequestParam(value = "boardType", required = false) String boardType, Model model) {
        model.addAttribute("boardType", boardType);
        model.addAttribute("boardList", adminBoardService.getBoards(boardType));
        return "admin/board/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("board", adminBoardService.createNotice());
        model.addAttribute("isEdit", false);
        return "admin/board/form";
    }

    @GetMapping("/edit/{boardNo}")
    public String editForm(@PathVariable("boardNo") Long boardNo, Model model) {
        model.addAttribute("board", adminBoardService.getBoard(boardNo));
        model.addAttribute("isEdit", true);
        return "admin/board/form";
    }

    @GetMapping("/detail/{boardNo}")
    public String detail(@PathVariable("boardNo") Long boardNo, Model model) {
        Board board = adminBoardService.getBoard(boardNo);
        model.addAttribute("board", board);
        model.addAttribute("isNotice", "NOTICE".equalsIgnoreCase(board.getBoardType()));
        return "admin/board/detail";
    }

    @PostMapping("/save")
    public String save(Board board, HttpSession session) {
        Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");
        adminBoardService.saveNotice(board, loginAdmin);
        return "redirect:/admin/board/list?boardType=NOTICE";
    }

    @PostMapping("/answer/{boardNo}")
    public String answer(@PathVariable("boardNo") Long boardNo,
                         @RequestParam("answerContent") String answerContent,
                         HttpSession session) {
        Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");
        adminBoardService.answerInquiry(boardNo, answerContent, loginAdmin);
        return "redirect:/admin/board/detail/" + boardNo;
    }

    @PostMapping("/delete/{boardNo}")
    public String delete(@PathVariable("boardNo") Long boardNo) {
        adminBoardService.deleteBoard(boardNo);
        return "redirect:/admin/board/list";
    }
}
