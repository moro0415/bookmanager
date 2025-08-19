package com.changhak.bookmanager.controller;

import com.changhak.bookmanager.domain.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 1. 세션에서 로그인한 관리자(Admin) 정보를 가져옴
 *    - 세션 키: "loginAdmin"
 *    - 세션에 값이 없으면 null
 *
 * 2. 로그인 상태 확인
 *    - 로그인하지 않은 경우(null이면) 로그인 페이지로 리다이렉트
 *    - 로그인된 경우 홈 화면(home/home.html) 반환
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session){
        Admin admin = (Admin) session.getAttribute("loginAdmin");
        if(admin == null){
            return "redirect:/login"; // 세션에 로그인 정보 없으면 로그인 화면으로 이동
        }
        return "home/home"; // 로그인된 경우 홈 화면 출력
    }
}
