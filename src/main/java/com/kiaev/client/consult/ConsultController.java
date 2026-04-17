package com.kiaev.client.consult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kiaev.client.car.Car;
import com.kiaev.client.car.CarService;
import com.kiaev.client.login.Login;
import com.kiaev.client.mypage.Wishlist;
import com.kiaev.client.mypage.WishlistService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/consult")
@RequiredArgsConstructor
public class ConsultController {

    private final ConsultService consultationService;
    private final WishlistService wishlistService;
    private final CarService carService;

    @ResponseBody
    @GetMapping("/form")
    public String showForm(@RequestParam(value = "carNo", required = false) Long carNo,
                           @RequestParam(value = "carName", required = false) String carName,
                           HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("prevPage", "/consult/formPage");
            return "<script>"
                    + "alert('로그인 후에 이용 가능한 서비스 입니다.');"
                    + "location.href='/login';"
                    + "</script>";
        }

        String redirectUrl = "/consult/formPage";
        boolean hasParam = false;

        if (carNo != null) {
            redirectUrl += (hasParam ? "&" : "?") + "carNo=" + carNo;
            hasParam = true;
        }

        if (carName != null && !carName.isEmpty()) {
            redirectUrl += (hasParam ? "&" : "?") + "carName=" + carName;
        }

        return "<script>"
                + "location.href='" + redirectUrl + "';"
                + "</script>";
    }

    @GetMapping("/formPage")
    public String formPage(@RequestParam(value = "carNo", required = false) Long carNo,
                           @RequestParam(value = "carName", required = false) String carName,
                           HttpSession session,
                           Model model) {

        Object loginUserObj = session.getAttribute("loginUser");

        if (loginUserObj == null) {
            return "redirect:/consult/form";
        }

        List<Car> allCarList = carService.findAll();

        List<Wishlist> wishlist = wishlistService.getMyWishlist(loginUserObj);
        List<Car> favoriteCarList = new ArrayList<>();
        Set<Long> favoriteCarNoSet = new HashSet<>();

        for (Wishlist item : wishlist) {
            if (item.getCar() != null) {
                Car car = item.getCar();
                favoriteCarList.add(car);
                favoriteCarNoSet.add(car.getCarNo());
            }
        }

        Long selectedCarNo = null;

        // 상세페이지에서 구매 상담 신청하기 누른 경우 현재 차량 자동선택
        if (carNo != null) {
            selectedCarNo = carNo;
        }
        // 그냥 상담신청 메뉴로 들어왔고 관심차량이 1개면 자동선택
        else if (favoriteCarList.size() == 1) {
            selectedCarNo = favoriteCarList.get(0).getCarNo();
        }

        model.addAttribute("allCarList", allCarList);
        model.addAttribute("favoriteCarList", favoriteCarList);
        model.addAttribute("favoriteCarNoSet", favoriteCarNoSet);
        model.addAttribute("selectedCarNo", selectedCarNo);
        model.addAttribute("selectedCar", carName);

        return "client/consult/consultForm";
    }

    @PostMapping("/register")
    public String submitForm(Consult consultation, HttpSession session) {

        Object loginUserObj = session.getAttribute("loginUser");

        if (loginUserObj == null) {
            return "redirect:/consult/form";
        }

        if (!(loginUserObj instanceof Login)) {
            return "redirect:/consult/form";
        }

        Login loginUser = (Login) loginUserObj;

        if (loginUser.getMemberNo() == null) {
            return "redirect:/consult/form";
        }

        consultation.setMemberNo(loginUser.getMemberNo());

        if (consultation.getConsultStatus() == null || consultation.getConsultStatus().isBlank()) {
            consultation.setConsultStatus("대기");
        }

        consultationService.save(consultation);

        return "redirect:/consult/success";
    }

    @GetMapping("/success")
    public String successPage(HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            return "redirect:/consult/form";
        }

        return "client/consult/success";
    }

    @GetMapping("/detail")
    public String consultDetail(@RequestParam("id") Long id, Model model, HttpSession session) {

        Object loginUserObj = session.getAttribute("loginUser");

        if (loginUserObj == null) {
            return "redirect:/consult/form";
        }

        if (!(loginUserObj instanceof Login)) {
            return "redirect:/consult/form";
        }

        Login loginUser = (Login) loginUserObj;

        Long memberNo = loginUser.getMemberNo();
        String memberStatus = loginUser.getMemberStatus();

        if (!isAdminOrDealer(memberStatus) && memberNo == null) {
            return "redirect:/consult/form";
        }

        Consult consult;

        if (isAdminOrDealer(memberStatus)) {
            consult = consultationService.findById(id);
        } else {
            consult = consultationService.findByConsultNoAndMemberNo(id, memberNo);
        }

        if (consult == null) {
            return "redirect:/consult/form";
        }

        model.addAttribute("consult", consult);
        model.addAttribute("member", loginUser);
        model.addAttribute("memberStatus", memberStatus);

        return "client/consult/consultDetail";
    }

    @PostMapping("/updateStatus")
    public String updateConsultStatus(@RequestParam("consultNo") Long consultNo,
                                      @RequestParam("consultStatus") String consultStatus,
                                      HttpSession session) {

        Object loginUserObj = session.getAttribute("loginUser");

        if (loginUserObj == null) {
            return "redirect:/login";
        }

        if (!(loginUserObj instanceof Login)) {
            return "redirect:/login";
        }

        Login loginUser = (Login) loginUserObj;

        if (!isAdminOrDealer(loginUser.getMemberStatus())) {
            return "redirect:/mypage/consult";
        }

        consultationService.updateConsultStatus(consultNo, consultStatus, loginUser.getMemberNo());

        return "redirect:/mypage/consult";
    }

    private boolean isAdminOrDealer(String memberStatus) {
        if (memberStatus == null) {
            return false;
        }

        return memberStatus.equalsIgnoreCase("ADMIN")
                || memberStatus.equalsIgnoreCase("DEALER")
                || memberStatus.equals("관리자")
                || memberStatus.equals("딜러");
    }
}
