package com.changhak.bookmanager.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 로그인 여부를 체크하는 인터셉터
 */
public class LoginCheckInterceptor implements HandlerInterceptor {

    /**
     * 컨트롤러 실행 전(preHandle) 세션 검사
     * - 로그인하지 않고 URL로 직접 특정 페이지 접근 시 여기서 차단됨
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        //세션이 없거나(loginAdmin 속성 없음) → 로그인 안 된 상태
        //→ 로그인 페이지로 리다이렉트 (/login?redirect=원래 URL)
        if(session == null || session.getAttribute("loginAdmin") == null){
            response.sendRedirect("/login?redirect=" + request.getRequestURI());
            return false; //→ 요청을 더 이상 진행하지 않음 (false 반환)
        }
        //세션에 로그인 정보 있으면 → 정상적으로 컨트롤러 실행 (true 반환)
        return true;
    }
}
