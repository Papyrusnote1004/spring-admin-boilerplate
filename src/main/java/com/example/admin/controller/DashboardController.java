package com.example.admin.controller;

import com.example.admin.repository.PostRepository;
import com.example.admin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userRepository.findAllActive().size());
        model.addAttribute("totalPosts", postRepository.findAllActive(PageRequest.of(0, 1)).getTotalElements());
        model.addAttribute("recentPosts", postRepository.findAllActive(PageRequest.of(0, 5)).getContent());
        model.addAttribute("pageTitle", "대시보드");
        return "dashboard";
    }
}
