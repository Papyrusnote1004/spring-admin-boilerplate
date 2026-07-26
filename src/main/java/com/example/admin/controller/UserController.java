package com.example.admin.controller;

import com.example.admin.model.entity.User;
import com.example.admin.model.enums.UserRole;
import com.example.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── 목록 ─────────────────────────────────────────────────────────────────
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("pageTitle", "사용자 관리");
        return "users/list";
    }

    // ── 등록 폼 ───────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("pageTitle", "사용자 등록");
        return "users/create";
    }

    // ── 등록 처리 ─────────────────────────────────────────────────────────────
    @PostMapping
    public String create(@ModelAttribute User user, RedirectAttributes ra) {
        try {
            userService.createUser(user);
            ra.addFlashAttribute("success", "사용자가 등록되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ── 상세 ─────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", user.getName());
        return "users/detail";
    }

    // ── 수정 폼 ───────────────────────────────────────────────────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        model.addAttribute("user", user);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("pageTitle", "사용자 수정");
        return "users/edit";
    }

    // ── 수정 처리 ─────────────────────────────────────────────────────────────
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute User form, RedirectAttributes ra) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
            user.setName(form.getName());
            user.setRole(form.getRole());
            user.setActive(form.isActive());
            userService.updateUser(user);
            ra.addFlashAttribute("success", "사용자 정보가 수정되었습니다.");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/users/" + id;
    }

    // ── 삭제 처리 ─────────────────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.deleteUser(id);
            ra.addFlashAttribute("success", "사용자가 삭제되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ── 비밀번호 초기화 ───────────────────────────────────────────────────────
    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id,
                                @RequestParam String newPassword,
                                RedirectAttributes ra) {
        try {
            userService.resetPassword(id, newPassword);
            ra.addFlashAttribute("success", "비밀번호가 초기화되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "비밀번호 초기화 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/users/" + id;
    }
}
