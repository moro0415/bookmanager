package com.changhak.bookmanager.service;

import com.changhak.bookmanager.domain.Admin;
import com.changhak.bookmanager.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Admin login(String loginId, String password){
        Admin admin = adminRepository.findByLoginId(loginId);
        if(admin == null){
            return null;
        }

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            return null;
        }

        return admin;
    }
}
