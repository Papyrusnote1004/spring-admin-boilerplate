package com.example.admin.controller;

import com.example.admin.model.dto.CustomUserDetails;
import com.example.admin.model.entity.Post;
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
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // ── 목록 ─────────────────────────────────────────────────────────────────
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        Page<Post> posts = postService.getPosts(page, size, keyword);
        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "게시글 목록");
        return "posts/list";
    }

    // ── 작성 폼 ───────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("pageTitle", "게시글 작성");
        return "posts/create";
    }

    // ── 작성 처리 ─────────────────────────────────────────────────────────────
    @PostMapping
    public String create(@Valid @ModelAttribute Post post,
                         BindingResult result,
                         Authentication authentication,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "posts/create";
        }
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            post.setAuthor(userDetails.getUser());
            postService.createPost(post);
            ra.addFlashAttribute("success", "게시글이 등록되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/posts";
    }

    // ── 상세 ─────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        postService.incrementViewCount(id);
        model.addAttribute("post", post);
        model.addAttribute("pageTitle", post.getTitle());
        return "posts/detail";
    }

    // ── 수정 폼 ───────────────────────────────────────────────────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        model.addAttribute("post", post);
        model.addAttribute("pageTitle", "게시글 수정");
        return "posts/edit";
    }

    // ── 수정 처리 ─────────────────────────────────────────────────────────────
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Post form,
                         BindingResult result,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "posts/edit";
        }
        try {
            Post post = postService.getPostById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
            post.setTitle(form.getTitle());
            post.setContent(form.getContent());
            postService.updatePost(post);
            ra.addFlashAttribute("success", "게시글이 수정되었습니다.");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/posts/" + id;
    }

    // ── 삭제 처리 ─────────────────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            postService.deletePost(id);
            ra.addFlashAttribute("success", "게시글이 삭제되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/posts";
    }
}
