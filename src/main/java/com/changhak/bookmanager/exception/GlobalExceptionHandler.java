package com.changhak.bookmanager.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException ex, Model model) {
        log.warn("404 자원 없음", ex);
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("Unhandled 예외 발생", ex);
        return "error/500";
    }
}

