package com.changhak.bookmanager.service;

import com.changhak.bookmanager.domain.Member;
import com.changhak.bookmanager.dto.SearchCondition;
import com.changhak.bookmanager.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final LoanService loanService;


    public Member save(Member member){
        member.setCreatedDate(LocalDateTime.now());
        int result = memberRepository.save(member);
        if(result != 1){
            throw new IllegalStateException("회원 저장 실패");
        }
        return findById(member.getId());
    }

    public List<Member> findAll(){
        return memberRepository.findAll();
    }

    public Member findById(Long id){
        return Optional.ofNullable(memberRepository.findById(id))
                .filter(member -> !member.isDeleted()) // 삭제되지 않은 회원만 허용
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 회원입니다. ID=" + id));
    }

    public Member update(Member member){
        int result = memberRepository.update(member);
        if(result != 1){
            throw new IllegalStateException("회원 수정 실패");
        }
        return findById(member.getId());
    }

    public void withdraw(Long id){
        if (loanService.isMemberCurrentlyLoaning(id)) {
            throw new IllegalStateException("해당 회원은 대출 중인 도서를 보유하고 있어 탈퇴할 수 없습니다.");
        }

        int result = memberRepository.withdraw(id);
        if (result != 1){
            throw new IllegalStateException("회원 탈퇴 처리 실패");
        }
    }

    public void restore(Long id){
        int result = memberRepository.restore(id);
        if (result != 1){
            throw new IllegalStateException("회원 복구 실패");
        }
    }

    public void delete(Long id) {
        Member member = findById(id);

        // 조건 1: 탈퇴하지 않은 회원은 삭제 불가
        if (!member.isWithdrawn()) {
            throw new IllegalStateException("탈퇴하지 않은 회원은 삭제할 수 없습니다.");
        }

        // 조건 2: 아직 반납하지 않은 도서를 대출 중인 회원은 삭제 불가
        if (loanService.isMemberCurrentlyLoaning(id)) {
            throw new IllegalStateException("반납하지 않은 도서를 보유 중인 회원은 삭제할 수 없습니다.");
        }

        int result = memberRepository.delete(id);

        if (result != 1) {
            throw new IllegalStateException("회원 완전 삭제 실패");
        }
    }


    public List<Member> search(SearchCondition condition) {
        return memberRepository.search(condition);
    }

    public int count(SearchCondition condition) {
        return memberRepository.count(condition);
    }
}

