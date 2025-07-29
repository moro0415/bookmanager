package com.changhak.bookmanager.service;

import com.changhak.bookmanager.domain.Book;
import com.changhak.bookmanager.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final BookRepository bookRepository;

    public void decreaseStock(Long bookId){
        Book book = validateBookExists(bookId);
        validateAvailableStock(book);
        book.setStock(book.getStock() - 1);
        int result = bookRepository.updateStock(book);
        if (result != 1) {
            throw new IllegalStateException("재고 감소에 실패했습니다.");
        }
    }

    public void increaseStock(Long bookId) {
        Book book = validateBookExists(bookId);
        book.setStock(book.getStock() + 1);
        int result = bookRepository.updateStock(book);
        if (result != 1) {
            throw new IllegalStateException("재고 증가에 실패했습니다.");
        }
    }

    private Book validateBookExists(Long bookId) {
        Book book = bookRepository.findById(bookId);
        if(book == null){
            throw new IllegalStateException("도서를 찾을 수 없습니다. ID=" + bookId);
        }
        return book;
    }

    private void validateAvailableStock(Book book) {
        if(book.getStock() <= 0){
            throw new IllegalStateException("재고가 부족합니다.");
        }
    }
}
