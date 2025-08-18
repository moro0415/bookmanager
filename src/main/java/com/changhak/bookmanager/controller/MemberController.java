package com.changhak.bookmanager.controller;

import com.changhak.bookmanager.domain.Member;
import com.changhak.bookmanager.dto.SearchCondition;
import com.changhak.bookmanager.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 회원 컨트롤러
 */
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /** 회원 목록 조회(검색/페이징) */
    @GetMapping
    public String list(@ModelAttribute("condition") SearchCondition condition,
                       Model model) {

        if(condition.getPage() < 1) {  // 음수/0 페이지 보정
            condition.setPage(1);
        }

        List<Member> members = memberService.search(condition);
        int totalCount = memberService.count(condition);
        int totalPages = (int) Math.ceil((double) totalCount / condition.getSize());

        model.addAttribute("members", members);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", condition.getPage());
        model.addAttribute("condition", condition);

        return "member/list";
    }

    /** 회원 등록 폼 */
    @GetMapping("/add")
    public String addForm(Model model){
        model.addAttribute("member", new Member());
        return "member/form";
    }

    /** 회원 등록 처리 */
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Member member, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "member/form";
        }
        Member savedMember = memberService.save(member);
        return "redirect:/members/" + savedMember.getId();
    }

    /** 회원 상세 조회 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model){
        Member member = memberService.findById(id);
        model.addAttribute("member", member);
        return "member/detail";
    }

    /** 회원 수정 폼 */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model){
        Member member = memberService.findById(id);
        model.addAttribute("member", member);
        return "member/form";
    }

    /** 회원 수정 처리 */
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute Member member, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "member/form";
        }
        member.setId(id);
        memberService.update(member);
        return "redirect:/members/" + id;
    }

    /** 회원 탈퇴 */
    @PostMapping("/withdraw/{id}")
    public String withdraw(@PathVariable Long id, @RequestHeader(value = "referer", required = false) String referer) {
        memberService.withdraw(id);

        // Referer 헤더 있으면 해당 페이지로, 없으면 기본 /members 로 이동 (refer = null 방어를 위해 분기 처리)
        return "redirect:" + (referer != null ? referer : "/members");
    }

    /** 회원 탈퇴 복구 */
    @PostMapping("/restore/{id}")
    public String restore(@PathVariable Long id, @RequestHeader(value = "referer", required = false) String referer) {
        memberService.restore(id);

        // Referer 헤더 있으면 해당 페이지로, 없으면 기본 /members 로 이동 (refer = null 방어를 위해 분기 처리)
        return "redirect:" + (referer != null ? referer : "/members");
    }

    /** 회원 삭제 */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        memberService.delete(id);
        return "redirect:/members";
    }
}


