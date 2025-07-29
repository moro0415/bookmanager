package com.changhak.bookmanager.repository;

import com.changhak.bookmanager.domain.Member;
import com.changhak.bookmanager.dto.SearchCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberRepository {

    int save(Member member);
    List<Member> findAll();
    Member findById(Long id);
    int update(Member member);

    int withdraw(Long id);     // 탈퇴 처리
    int restore(Long id);      // 탈퇴 복구
    int delete(Long id);       // 완전 삭제

    List<Member> search(SearchCondition condition);
    int count(SearchCondition condition);
}
