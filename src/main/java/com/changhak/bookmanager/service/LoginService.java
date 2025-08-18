package com.changhak.bookmanager.service;

import com.changhak.bookmanager.domain.Admin;
import com.changhak.bookmanager.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 관리자 로그인 서비스
 */
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Admin login(String loginId, String password){
        Admin admin = adminRepository.findByLoginId(loginId);

        //로그인 실패 시 예외 대신 null 반환 (LoginController에서 분기 처리)
        if(admin == null){
            return null;
        }

        //BCryptPasswordEncoder SecurityConfig에서 빈 등록)를 사용해 해시 기반 비밀번호 검증
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            return null;
        }

        return admin;
    }
}
