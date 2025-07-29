package com.changhak.bookmanager.service;

import com.changhak.bookmanager.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    void save(){
        Member member = new Member();
        member.setName("고길동");
        member.setEmail("111@gmail.com");
        member.setPhone("010-1111-1111");
        member.setAddress("서울특별시 동대문구");

        Member saved = memberService.save(member);
        Member found = memberService.findById(saved.getId());

        assertThat(saved.getId()).isEqualTo(found.getId());
    }

    @Test
    void findAll() {
        List<Member> members = memberService.findAll();
        assertThat(members).isNotEmpty();
    }

    @Test
    void delete() {
        Member member = new Member();
        member.setName("고길동");
        member.setEmail("111@gmail.com");
        member.setPhone("010-1111-1111");
        member.setAddress("서울특별시 동대문구");

        Member saved = memberService.save(member);
        memberService.delete(saved.getId());

        List<Member> members = memberService.findAll();
        boolean exists = members
                .stream()
                .anyMatch(b -> b.getId().equals(saved.getId()));
        assertThat(exists).isFalse();
    }
}