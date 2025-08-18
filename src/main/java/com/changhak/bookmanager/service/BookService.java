package com.changhak.bookmanager.service;

import com.changhak.bookmanager.domain.Book;
import com.changhak.bookmanager.dto.SearchCondition;
import com.changhak.bookmanager.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 도서 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final LoanService loanService;

    /** 도서 저장 */
    public Book save(Book book){
        int result = bookRepository.save(book);
        if(result != 1){
            throw new IllegalStateException("도서 저장 실패");
        }
        return findById(book.getId());
    }

    /** 도서 전체 조회 */
    public List<Book> findAll(){
        return bookRepository.findAll();
    }

    /** 도서 단건 조회 */
    public Book findById(Long id){
        return Optional.ofNullable(bookRepository.findById(id))
                .filter(book -> !book.isDeleted()) // 삭제되지 않은 도서만 허용
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 도서입니다. ID=" + id));
    }

    /** 도서 정보 수정 */
    public Book update(Book book) {
        // 기존 책 정보 조회
        Book origin = findById(book.getId());

        // 재고 방어 로직: 관리자가 기존보다 재고를 줄이려는 시도를 무시
        if (book.getStock() < origin.getStock()) {
            book.setStock(origin.getStock());
        }

        // DB 업데이트
        int result = bookRepository.update(book);
        if (result != 1) {
            throw new IllegalStateException("도서 수정 실패");
        }

        // 수정 후 반영된 결과 반환
        return findById(book.getId());
    }

    /** 도서 삭제 */
    public void delete(Long id) {
        if (loanService.isBookCurrentlyLoaned(id)) {
            throw new IllegalStateException("대출 중인 도서는 삭제할 수 없습니다.");
        }

        int result = bookRepository.delete(id);

        if (result != 1) {
            throw new IllegalStateException("도서 삭제 실패");
        }
    }
    /** 도서 검색 처리 */
    public List<Book> search(SearchCondition condition){
        return bookRepository.search(condition);
    }
    /** 페이징용 도서 수 카운트 */
    public int count(SearchCondition condition) {
        return bookRepository.count(condition);
    }
}
