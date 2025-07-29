package com.changhak.bookmanager.service;

import com.changhak.bookmanager.domain.Loan;
import com.changhak.bookmanager.domain.Member;
import com.changhak.bookmanager.dto.LoanDto;
import com.changhak.bookmanager.dto.SearchCondition;
import com.changhak.bookmanager.repository.LoanRepository;
import com.changhak.bookmanager.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final InventoryService inventoryService;

    // 대출 등록
    public Loan createLoan(Long bookId, Long memberId) {
        // 중복 대출 방지
        Loan existingLoan = loanRepository.findActiveLoan(bookId, memberId);
        if (existingLoan != null) {
            throw new IllegalStateException("해당 도서는 이미 대출 중입니다.");
        }

        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("해당 회원을 찾을 수 없습니다. ID=" + memberId);
        }
        inventoryService.decreaseStock(bookId);

        Loan loan = new Loan();
        loan.setBookId(bookId);
        loan.setMemberId(memberId);
        loan.setLoanDate(LocalDate.now());

        int result = loanRepository.save(loan);
        if (result != 1) {
            throw new IllegalStateException("대출 저장에 실패했습니다.");
        }

        return loan;
    }

    // 반납 처리
    public Loan returnLoan(Long loanId) {
        Loan loan = findById(loanId);

        inventoryService.increaseStock(loan.getBookId());

        int result = loanRepository.returnBook(loanId);
        if (result != 1) {
            throw new IllegalStateException("반납 처리에 실패했습니다.");
        }

        return loan;
    }

    // 전체 대출 목록
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    // 단건 조회
    public Loan findById(Long id) {
        return Optional.ofNullable(loanRepository.findById(id))
                .orElseThrow(() -> new IllegalArgumentException("대출 정보를 찾을 수 없습니다. ID=" + id));
    }

    // 삭제
    public void delete(Long id) {
        Loan loan = findById(id);

        if (loan.getReturnDate() == null) {
            throw new IllegalStateException("반납되지 않은 대출은 삭제할 수 없습니다.");
        }

        int result = loanRepository.delete(id);
        if (result != 1) {
            throw new IllegalStateException("대출 삭제에 실패했습니다.");
        }
    }


    // 🔒 삭제 제약용: 해당 책이 대출 중인지 확인
    public boolean isBookCurrentlyLoaned(Long bookId) {
        return loanRepository.existsUnreturnedLoanByBookId(bookId);
    }

    // 🔒 삭제 제약용: 해당 회원이 대출 중인지 확인
    public boolean isMemberCurrentlyLoaning(Long memberId) {
        return loanRepository.existsUnreturnedLoanByMemberId(memberId);
    }

    public List<LoanDto> search(SearchCondition condition){
        return loanRepository.search(condition);
    }

    public int count(SearchCondition condition) {
        return loanRepository.count(condition);
    }

}
