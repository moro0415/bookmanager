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


/**
 * 로그인/로그아웃 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;


    /** 로그인 폼 */
    //에러 파라미터 있으면 오류 메시지 표시(아래 login 메서드에서 로그인 처리 실패시 에러 파라미터 넘어옴)
    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false)
                            String error,
                            Model model) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("loginError", error != null);

        return "login/login";
    }

    /** 로그인 처리 */
    //성공 시 세션 저장, 실패 시 loginForm 메서드로 get 요청 리다이렉트 + 에러 파라미터
    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm form, HttpSession session) {
        Admin admin = loginService.login(form.getLoginId(), form.getPassword());

        //로그인 실패시 null 분기 처리
        if (admin == null) {
            return "redirect:/login?error=true";
        }

        session.setAttribute("loginAdmin", admin);

        return "redirect:/";
    }

    /** 로그아웃 처리 */
    //세션 무효화 후 로그인 페이지로 리다이렉트
    @PostMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}
