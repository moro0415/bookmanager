package com.changhak.bookmanager.dto;

import com.changhak.bookmanager.domain.Book;
import com.changhak.bookmanager.domain.Loan;
import com.changhak.bookmanager.domain.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoanDto {// 마이바티스가 기본 생성자 사용하기 위해 final 제거
    private Loan loan;
    private Book book;
    private Member member;


    public LoanDto(Loan loan, Book book, Member member) {
        this.loan = loan;
        this.book = book;
        this.member = member;
    }
}
