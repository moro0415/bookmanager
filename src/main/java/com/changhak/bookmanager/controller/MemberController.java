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

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public String list(@ModelAttribute("condition") SearchCondition condition,
                       Model model) {

        if(condition.getPage() < 1) {
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

    @GetMapping("/add")
    public String addForm(Model model){
        model.addAttribute("member", new Member());
        return "member/form";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Member member, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "member/form";
        }
        Member savedMember = memberService.save(member); // 수정: 저장된 Member 반환받기
        return "redirect:/members/" + savedMember.getId(); // 수정: 상세 페이지로 이동
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model){
        Member member = memberService.findById(id);
        model.addAttribute("member", member);
        return "member/detail";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model){
        Member member = memberService.findById(id);
        model.addAttribute("member", member);
        return "member/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute Member member, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "member/form";
        }
        member.setId(id);
        memberService.update(member);
        return "redirect:/members/" + id;
    }

    @PostMapping("/withdraw/{id}")
    public String withdraw(@PathVariable Long id, @RequestHeader(value = "referer", required = false) String referer) {
        memberService.withdraw(id);
        return "redirect:" + (referer != null ? referer : "/members");
    }

    @PostMapping("/restore/{id}")
    public String restore(@PathVariable Long id, @RequestHeader(value = "referer", required = false) String referer) {
        memberService.restore(id);
        return "redirect:" + (referer != null ? referer : "/members");
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        memberService.delete(id);
        return "redirect:/members";
    }
}


