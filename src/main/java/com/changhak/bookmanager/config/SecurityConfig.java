package com.changhak.bookmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 보안 설정
 * - 비밀번호 해시용 BCryptPasswordEncoder 빈 등록
 * - 시큐리티 설정
 */
@Configuration
public class SecurityConfig {

    /** 비밀번호 해시용 빈 */
    /** - LoginService에서 비밀 번호 검증에 사용 */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    /** 시큐리티 설정 */
    /** - 최소 보안 설정 */
    /** - 모든 요청 허용 */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable());

        return http.build();
    }
}
