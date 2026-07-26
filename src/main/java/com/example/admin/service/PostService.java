package com.example.admin.service;

import com.example.admin.model.entity.Post;
import com.example.admin.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<Post> getPosts(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return postRepository.searchByKeyword(keyword.trim(), pageable);
        }
        return postRepository.findAllActive(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPostById(Long id) {
        return postRepository.findActiveById(id);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Post not found: " + id));
        post.setDeleted(true);
        postRepository.save(post);
    }

    public void incrementViewCount(Long id) {
        postRepository.incrementViewCount(id);
    }
}
