package com.changhak.bookmanager.service;

import com.changhak.bookmanager.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BookServiceTest {

    @Autowired
    BookService bookService;

    @Test
    void save(){
        Book book = new Book();
        book.setTitle("test book");
        book.setAuthor("홍길동");
        book.setPublisher("test publisher");
        book.setIsbn("11112222");
        book.setStock(2);

        Book saved = bookService.save(book);
        Book found = bookService.findById(saved.getId());

        assertThat(saved.getId()).isEqualTo(found.getId());
    }

    @Test
    void findAll() {
        List<Book> books = bookService.findAll();
        assertThat(books).isNotEmpty();
    }

    @Test
    void delete() {
        Book book = new Book();
        book.setTitle("삭제용 책");
        book.setAuthor("저자");
        book.setPublisher("출판사");
        book.setIsbn("9999888877776");
        book.setStock(1);

        Book saved = bookService.save(book);
        bookService.delete(saved.getId());

        List<Book> books = bookService.findAll();
        boolean exists = books
                .stream()
                .anyMatch(b -> b.getId().equals(saved.getId()));
        assertThat(exists).isFalse();
    }
}