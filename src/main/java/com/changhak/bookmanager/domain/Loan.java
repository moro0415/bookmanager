package com.changhak.bookmanager.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Loan {
    private Long id;
    private Long bookId;
    private Long memberId;
    private LocalDate loanDate;
    private LocalDate returnDate;
}
