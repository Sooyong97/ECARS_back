package com.sbb.ecars.controller;

import com.sbb.ecars.dto.PostDto;
import com.sbb.ecars.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 모든 게시글 가져오기
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 특정 id 게시글 가져오기
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(postService.createPost(userId, title, content, file));
    }

    // 게시글 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @RequestParam Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(postService.updatePost(id, title, content, file));
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
