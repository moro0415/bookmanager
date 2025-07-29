package com.changhak.bookmanager.repository;

import com.changhak.bookmanager.domain.Loan;
import com.changhak.bookmanager.dto.LoanDto;
import com.changhak.bookmanager.dto.SearchCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoanRepository {
    int save(Loan loan);

    int returnBook(Long id);

    int delete(Long id);

    Loan findById(Long id);

    List<Loan> findAll();

    Loan findActiveLoan(Long bookId, Long memberId);

    boolean existsUnreturnedLoanByBookId(Long bookId);

    boolean existsUnreturnedLoanByMemberId(Long memberId);

    List<LoanDto> search(SearchCondition condition);

    int count(SearchCondition condition);
}
