package com.changhak.bookmanager.controller;

import com.changhak.bookmanager.domain.Admin;
import com.changhak.bookmanager.dto.LoginForm;
import com.changhak.bookmanager.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false)
                            String error,
                            Model model) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("loginError", error != null);

        return "login/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm form, HttpSession session) {
        Admin admin = loginService.login(form.getLoginId(), form.getPassword());

        if (admin == null) {
            return "redirect:/login?error=true";
        }

        session.setAttribute("loginAdmin", admin);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}
