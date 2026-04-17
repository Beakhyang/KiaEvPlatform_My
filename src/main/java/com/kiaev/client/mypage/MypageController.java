package com.kiaev.client.mypage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kiaev.client.consult.Consult;
import com.kiaev.client.consult.ConsultService;
import com.kiaev.client.login.Login;
import com.kiaev.client.login.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MypageController {

   @Autowired
   private MypageService mypageService;

   @Autowired
   private LoginService loginService;

   @Autowired
   private ConsultService consultService;

   @GetMapping("/mypage")
   public String mypageMain(HttpSession session) {
      Login loginUser = (Login) session.getAttribute("loginUser");

      if (loginUser == null) {
         return "redirect:/session-expired";
      }

      return "redirect:/mypage/myinfo";
   }

   @GetMapping("/mypage/myinfo")
   public String myInfo(HttpSession session, Model model) {
      Login loginUser = (Login) session.getAttribute("loginUser");

      if (loginUser == null) {
         return "redirect:/session-expired";
      }

      Mypage memberInfo = mypageService.getMemberInfo(loginUser.getMemberNo());

      model.addAttribute("loginUser", loginUser);
      model.addAttribute("memberInfo", memberInfo);

      return "client/mypage/myinfo";
   }

   @PostMapping("/mypage/update")
   public String updateMyInfo(Mypage mypage, HttpSession session) {
      Login loginUser = (Login) session.getAttribute("loginUser");

      if (loginUser == null) {
         return "redirect:/session-expired";
      }

      mypage.setMemberNo(loginUser.getMemberNo());
      mypageService.updateMemberInfo(mypage);

      return "redirect:/mypage/myinfo";
   }

   @GetMapping("/mypage/passwordChange")
   public String passwordForm(HttpSession session, Model model) {
      Login loginUser = (Login) session.getAttribute("loginUser");

      if (loginUser == null && !model.containsAttribute("passwordChangeSuccess")) {
         return "redirect:/session-expired";
      }

      return "client/mypage/passwordchange";
   }

   @PostMapping("/mypage/passwordChange")
   public String passwordChange(@RequestParam("currentPw") String currentPw,
         @RequestParam("newPw") String newPw,
         @RequestParam("confirmPw") String confirmPw,
         HttpSession session, RedirectAttributes redirectAttributes) {

      Login loginUser = (Login) session.getAttribute("loginUser");

      if (loginUser == null) {
         return "redirect:/session-expired";
      }

      if (!newPw.equals(confirmPw)) {
         redirectAttributes.addFlashAttribute("errorMessage", "새 비밀번호가 일치하지 않습니다.");
         return "redirect:/mypage/passwordChange";
      }

      boolean result = mypageService.changePassword(loginUser.getMemberNo(), currentPw, newPw);

      if (!result) {
         redirectAttributes.addFlashAttribute("errorMessage", "현재 비밀번호가 올바르지 않습니다.");
         return "redirect:/mypage/passwordChange";
      }

      session.invalidate();
      redirectAttributes.addFlashAttribute("passwordChangeSuccess", true);

      return "redirect:/mypage/passwordChange";
   }

   @GetMapping("/mypage/delete")
   public String deleteMember(HttpSession session) {
      Login loginUser = (Login) session.getAttribute("loginUser");

      if (loginUser == null) {
         return "redirect:/session-expired";
      }

      mypageService.deleteMember(loginUser.getMemberNo());
      session.invalidate();

      return "redirect:/login";
   }

   @GetMapping("/mypage/consult")
   public String consultHistory(HttpSession session, Model model) {
      Login loginUser = (Login) session.getAttribute("loginUser");

      if (loginUser == null) {
         return "redirect:/session-expired";
      }

      List<Consult> list = consultService.findByMemberNo(loginUser.getMemberNo());

      model.addAttribute("consultList", list);
      model.addAttribute("loginUser", loginUser);

      return "client/mypage/consulthistory";
   }

   @GetMapping("/mypage/board")
   public String boardHistory(HttpSession session, Model model) {
      Login loginUser = (Login) session.getAttribute("loginUser");

      if (loginUser == null) {
         return "redirect:/session-expired";
      }

      List<Mypage> list = mypageService.getBoardHistory(loginUser.getMemberNo());

      model.addAttribute("boardList", list);
      model.addAttribute("loginUser", loginUser);

      return "client/mypage/boardhistory";
   }

   @GetMapping("/mypage/update")
   public String updateForm(HttpSession session, Model model) {
      Login loginUser = (Login) session.getAttribute("loginUser");

      if (loginUser == null) {
         return "redirect:/session-expired";
      }

      Mypage memberInfo = mypageService.getMemberInfo(loginUser.getMemberNo());

      model.addAttribute("loginUser", loginUser);
      model.addAttribute("memberInfo", memberInfo);

      return "client/mypage/update";
   }
}
