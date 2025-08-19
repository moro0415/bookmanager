package com.changhak.bookmanager.service;

import com.changhak.bookmanager.domain.Book;
import com.changhak.bookmanager.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 재고 변경 전담 서비스 (순환 의존성 차단용)
 * 기존 : LoanService -> BookService -> LoanService (순환 구조 문제)
 * 변경 : LoanService -> InventoryService -> BookRepository
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final BookRepository bookRepository;

    /** 재고 -1 (대출 시) */
    public void decreaseStock(Long bookId){
        Book book = validateBookExists(bookId); // 도서 존재 여부 검증
        validateAvailableStock(book); // 잔여 재고 존재 여부 검증 (재고 > 0 확인)
        book.setStock(book.getStock() - 1);
        int result = bookRepository.updateStock(book);
        if (result != 1) {
            throw new IllegalStateException("재고 감소에 실패했습니다.");
        }
    }

    /** 재고 +1 (반납 시) */
    public void increaseStock(Long bookId) {
        Book book = validateBookExists(bookId);
        book.setStock(book.getStock() + 1);
        int result = bookRepository.updateStock(book);
        if (result != 1) {
            throw new IllegalStateException("재고 증가에 실패했습니다.");
        }
    }

    /** 도서 존재 여부 검증: 없으면 예외 */
    private Book validateBookExists(Long bookId) {
        Book book = bookRepository.findById(bookId);
        if(book == null){
            throw new IllegalStateException("도서를 찾을 수 없습니다. ID=" + bookId);
        }
        return book;
    }

    /** 잔여 재고 존재 여부 검증: 없으면 예외 */
    private void validateAvailableStock(Book book) {
        if(book.getStock() <= 0){
            throw new IllegalStateException("재고가 부족합니다.");
        }
    }
}
