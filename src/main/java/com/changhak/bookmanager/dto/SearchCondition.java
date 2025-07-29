package com.changhak.bookmanager.dto;

import lombok.Data;

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
