package com.changhak.bookmanager.dto;

import lombok.Data;

/**
 * LoginForm
 * - 로그인 시 입력받는 값(loginId, password)만 따로 담는 DTO
 * - 도메인(Admin)과 분리하여 요청 파라미터 검증 전용으로 사용
 */
@Data
public class LoginForm {
    private String loginId;
    private String password;
}
