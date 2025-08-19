package com.changhak.bookmanager.dto;

import com.changhak.bookmanager.domain.Book;
import com.changhak.bookmanager.domain.Loan;
import com.changhak.bookmanager.domain.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoanDto
 * - Loan, Book, Member 엔티티를 묶어서
 *   화면에 필요한 데이터를 한번에 전달하기 위한 DTO
 * - 대출 목록/상세 화면에서 필요한 대출, 도서, 회원 정보들을 한번에 사용 가능
 */
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
