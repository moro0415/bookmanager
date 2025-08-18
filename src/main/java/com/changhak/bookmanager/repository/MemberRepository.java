package com.changhak.bookmanager.repository;

import com.changhak.bookmanager.domain.Member;
import com.changhak.bookmanager.dto.SearchCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 회원 Repository
 * - 실제 SQL은 resources/mapper/MemberMapper.xml에 정의
 */
@Mapper
public interface MemberRepository {

    int save(Member member);
    List<Member> findAll();
    Member findById(Long id);
    int update(Member member);

    int withdraw(Long id);
    int restore(Long id);
    int delete(Long id);

    List<Member> search(SearchCondition condition);
    int count(SearchCondition condition);
}
