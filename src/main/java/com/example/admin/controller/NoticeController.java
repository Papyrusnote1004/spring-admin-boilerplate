package com.example.admin.controller;

import com.example.admin.model.dto.CustomUserDetails;
import com.example.admin.model.entity.Notice;
import com.example.admin.model.entity.Post;
import com.example.admin.service.NoticeService;
import com.example.admin.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // ── 목록 ─────────────────────────────────────────────────────────────────
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        Page<Notice> notices = noticeService.getNotices(page, size, keyword);
        model.addAttribute("notices", notices);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "공지사항 목록");
        return "notices/list";
    }

    // ── 작성 폼 ───────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("notice", new Notice());
        model.addAttribute("pageTitle", "공지사항 작성");
        return "notices/create";
    }

    // ── 작성 처리 ─────────────────────────────────────────────────────────────
    @PostMapping
    public String create(@Valid @ModelAttribute Notice notice,
                         BindingResult result,
                         Authentication authentication,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "notices/create";
        }
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            notice.setAuthor(userDetails.getUser());
            noticeService.createNotice(notice);
            ra.addFlashAttribute("success", "공지사항이 등록되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/notices";
    }

    // ── 상세 ─────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Notice notice = noticeService.getNoticeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        noticeService.incrementViewCount(id);
        model.addAttribute("notice", notice);
        model.addAttribute("pageTitle", notice.getTitle());
        return "notices/detail";
    }

    // ── 수정 폼 ───────────────────────────────────────────────────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Notice notice = noticeService.getNoticeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        model.addAttribute("notice", notice);
        model.addAttribute("pageTitle", "게시글 수정");
        return "notices/edit";
    }

    // ── 수정 처리 ─────────────────────────────────────────────────────────────
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Post form,
                         BindingResult result,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "notices/edit";
        }
        try {
            Notice notice = noticeService.getNoticeById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
            notice.setTitle(form.getTitle());
            notice.setContent(form.getContent());
            noticeService.updateNotice(notice);
            ra.addFlashAttribute("success", "게시글이 수정되었습니다.");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/notices/" + id;
    }

    // ── 삭제 처리 ─────────────────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            noticeService.deleteNotice(id);
            ra.addFlashAttribute("success", "게시글이 삭제되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/notices";
    }
}
