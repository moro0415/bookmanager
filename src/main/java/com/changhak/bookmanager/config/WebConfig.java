package com.changhak.bookmanager.config;

import com.changhak.bookmanager.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig
 * - 애플리케이션 전역 설정 담당
 * - 인터셉터 등록 (로그인 체크)
 * - 정적 리소스 매핑 (업로드된 이미지/썸네일 제공)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 인터셉터 설정
     * - LoginCheckInterceptor를 등록해서 로그인 여부 확인
     * - 모든 경로("/**")에 적용하되, 아래 경로들은 예외 처리
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/login",
                        "/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error",
                        "/favicon.ico"
                );
    }

    /**
     * 정적 리소스 핸들러 설정
     * - 브라우저에서 "/upload/**" 경로로 요청하면
     * 서버 로컬 실행 경로의 /upload/ 디렉토리 매핑
     * - 도서 이미지 / 썸네일 파일을 직접 제공하기 위해 설정
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 썸네일, 이미지 정적 리소스 매핑
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/upload/");
    }

}
