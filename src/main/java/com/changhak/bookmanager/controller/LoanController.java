package com.changhak.bookmanager.controller;

import com.changhak.bookmanager.domain.Book;
import com.changhak.bookmanager.domain.Loan;
import com.changhak.bookmanager.domain.Member;
import com.changhak.bookmanager.dto.LoanDto;
import com.changhak.bookmanager.dto.SearchCondition;
import com.changhak.bookmanager.service.BookService;
import com.changhak.bookmanager.service.LoanService;
import com.changhak.bookmanager.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 대출 컨트롤러
 */
@Controller
@RequestMapping("loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final MemberService memberService;

    /** 대출 목록 조회(검색/조건필터/페이징) */
    @GetMapping
    public String list(@ModelAttribute("condition") SearchCondition condition,
                       Model model) {

        if(condition.getPage() < 1) { // 음수/0 페이지 보정
            condition.setPage(1);
        }
        List<LoanDto> loanDtos = loanService.search(condition);
        int totalCount = loanService.count(condition);
        int totalPages = (int) Math.ceil((double) totalCount / condition.getSize());

        model.addAttribute("loans", loanDtos);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", condition.getPage());
        model.addAttribute("condition", condition);

        return "loan/list";
    }

    /** 대출 상세 조회 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model){
        Loan loan = loanService.findById(id);
        Book book = bookService.findById(loan.getBookId());
        Member member = memberService.findById(loan.getMemberId());

        LoanDto loanDto = new LoanDto(loan, book, member);
        model.addAttribute("loan", loanDto);
        return "loan/detail";
    }

    /** 대출 등록 폼 */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("loan", new Loan());

        List<Book> availableBooks = bookService.findAll()
                .stream()
                .filter(book -> book.getStock() > 0)
                .toList();
        model.addAttribute("books", availableBooks);

        List<Member> activeMembers = memberService.findAll()
                .stream()
                .filter(member -> !member.isWithdrawn())
                .toList();
        model.addAttribute("members", activeMembers);

        return "loan/form";
    }

    /** 대출 등록 처리 */
    @PostMapping("/add")
    public String add(@RequestParam Long bookId,
                      @RequestParam Long memberId) {
        Loan savedLoan = loanService.createLoan(bookId, memberId); // 수정: 등록된 Loan 반환
        return "redirect:/loans/" + savedLoan.getId();              // 수정: 상세 페이지로 이동
    }

    /** 대출 반납 처리 */
    @PostMapping("/return/{id}")
    public String returnLoan(@PathVariable Long id,
                             @RequestHeader(value = "Referer", required = false) String referer) {
        Loan loan = loanService.findById(id);

        if (loan.getReturnDate() != null){
            return "redirect:" + (referer != null ? referer : "/loans");
        }

        loanService.returnLoan(id);
        return "redirect:" + (referer != null ? referer : "/loans");
    }

    /** 대출 삭제 */
    //완전 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        loanService.delete(id);
        return "redirect:/loans";
    }
}
