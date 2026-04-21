package com.kiaev.admin;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.kiaev.admin.login.Admin;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class AdminModelAttributeAdvice {

    @ModelAttribute("loginAdmin")
    public Admin loginAdmin(HttpSession session) {
        Object value = session.getAttribute("loginAdmin");
        return value instanceof Admin admin ? admin : null;
    }
}
