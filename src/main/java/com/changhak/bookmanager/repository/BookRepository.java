package com.changhak.bookmanager.repository;

import com.changhak.bookmanager.domain.Book;
import com.changhak.bookmanager.dto.SearchCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * 도서 Repository
 * - 실제 SQL은 resources/mapper/BookMapper.xml에 정의
 */
@Mapper
public interface BookRepository {

    int save(Book book);  // insert 실행시 영향받은 행 수 리턴
    List<Book> findAll();
    Book findById(Long id);
    int update(Book book);
    int delete(Long id);

    List<Book> search(SearchCondition condition);

    int count(SearchCondition condition);

    int updateStock(Book book);
}
