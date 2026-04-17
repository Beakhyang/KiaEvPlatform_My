package com.kiaev.client.mypage;

import java.time.LocalDate;

public class Mypage {

    // 회원 정보
    private Long memberNo;
    private String loginId;
    private String memberName;
    private String email;
    private String phone;
    private String zipcode;
    private String address;
    private String detailAddress;

    // 상담 내역
    private Long consultNo;
    private String consultTitle;
    private String consultContent;
    private String consultStatus;
    private LocalDate consultDate;

    // 문의 내역
    private Long boardNo;
    private String boardTitle;
    private String boardContent;
    private LocalDate boardDate;
    private String boardStatus;   // 추가

    // 관심 차량
    private Long carNo;
    private String carName;
    private String carImage;
    private Integer carPrice;

    // =======================
    // getter / setter
    // =======================

    public Long getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(Long memberNo) {
        this.memberNo = memberNo;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public Long getConsultNo() {
        return consultNo;
    }

    public void setConsultNo(Long consultNo) {
        this.consultNo = consultNo;
    }

    public String getConsultTitle() {
        return consultTitle;
    }

    public void setConsultTitle(String consultTitle) {
        this.consultTitle = consultTitle;
    }

    public String getConsultContent() {
        return consultContent;
    }

    public void setConsultContent(String consultContent) {
        this.consultContent = consultContent;
    }

    public String getConsultStatus() {
        return consultStatus;
    }

    public void setConsultStatus(String consultStatus) {
        this.consultStatus = consultStatus;
    }

    public LocalDate getConsultDate() {
        return consultDate;
    }

    public void setConsultDate(LocalDate consultDate) {
        this.consultDate = consultDate;
    }

    public Long getBoardNo() {
        return boardNo;
    }

    public void setBoardNo(Long boardNo) {
        this.boardNo = boardNo;
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public String getBoardContent() {
        return boardContent;
    }

    public void setBoardContent(String boardContent) {
        this.boardContent = boardContent;
    }

    public LocalDate getBoardDate() {
        return boardDate;
    }

    public void setBoardDate(LocalDate boardDate) {
        this.boardDate = boardDate;
    }

    // 추가
    public String getBoardStatus() {
        return boardStatus;
    }

    // 추가
    public void setBoardStatus(String boardStatus) {
        this.boardStatus = boardStatus;
    }

    public Long getCarNo() {
        return carNo;
    }

    public void setCarNo(Long carNo) {
        this.carNo = carNo;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public Integer getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(Integer carPrice) {
        this.carPrice = carPrice;
    }
}