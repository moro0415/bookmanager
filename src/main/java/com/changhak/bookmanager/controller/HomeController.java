package com.changhak.bookmanager.controller;

import com.changhak.bookmanager.domain.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session){
        Admin admin = (Admin) session.getAttribute("loginAdmin");
        if(admin == null){
            return "redirect:/login";
        }
        return "home/home";
    }
}
