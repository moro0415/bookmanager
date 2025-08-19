package com.changhak.bookmanager.dto;

import lombok.Data;

/**
 * SearchCondition
 * - 검색/페이징 조건을 담는 DTO
 * - keyword, status, page, size 등을 컨트롤러 → 서비스 → 리포지토리까지 전달
 * - Book/Member/Loan 공통으로 재사용됨
 */
@Data
public class SearchCondition {

    private String keyword;

    private Integer page = 1;
    private Integer size = 10;
    private String status;//대출 검색용
    public int getOffset(){
        return (page - 1) * size;
    }
}
