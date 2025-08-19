package com.changhak.bookmanager.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 전역 예외 처리 클래스
 * - @ControllerAdvice: 모든 컨트롤러에서 발생하는 예외를 가로채 처리
 * - 예외 유형별로 @ExceptionHandler 메서드를 두어 사용자 친화적인 에러 화면 반환
 * - 각 예외를 잡아 로그를 남기고, 상황별 맞는 뷰(error/custom, error/404, error/500)를 반환
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /** DB 중복키 예외 처리 */
    @ExceptionHandler(DuplicateKeyException.class)
    public String handleDuplicateKeyException(DuplicateKeyException ex, Model model) {
        String message = "중복된 값이 존재합니다";

        String rawMessage = ex.getMessage();
        if (rawMessage != null) {
            if (rawMessage.contains("isbn")) {
                message = "이미 등록된 ISBN입니다";
            } else if (rawMessage.contains("email")) {
                message = "이미 등록된 이메일입니다";
            }
        }

        log.warn("DuplicateKeyException 발생", ex);
        model.addAttribute("errorMessage", message);
        return "error/custom";
    }

    /** 잘못된 요청 값 예외 처리 */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            message = "잘못된 요청입니다";
        }

        log.warn("IllegalArgumentException 발생", ex);
        model.addAttribute("errorMessage", message);
        return "error/custom";
    }

    /** 잘못된 상태 예외 처리 */
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, Model model) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            message = "잘못된 요청입니다";
        }

        log.warn("IllegalStateException 발생", ex);
        model.addAttribute("errorMessage", message);
        return "error/custom";
    }

    /** 잘못된 URL 요청(404) 처리 */
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException ex, Model model) {
        log.warn("404 자원 없음", ex);
        return "error/404";
    }

    /** 그 외 모든 예외(500) 처리 */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("Unhandled 예외 발생", ex);
        return "error/500";
    }
}

