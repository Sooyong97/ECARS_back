package com.sbb.ecars.service;

import com.sbb.ecars.domain.Post;
import com.sbb.ecars.dto.PostDto;
import com.sbb.ecars.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    //모든 게시글 찾기
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 게시글 id로 게시글 찾기
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        return PostDto.fromEntity(post);
    }

    // 게시글 생성
    public PostDto createPost(String userId, String title, String content, MultipartFile file) throws IOException {
        String filePath = saveFile(file);

        Post post = Post.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .filePath(filePath)
                .build();
        return PostDto.fromEntity(postRepository.save(post));
    }

    // 게시글 업데이트
    public PostDto updatePost(Long id, String userId, String title, String content, MultipartFile file) throws IOException {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

        post.setUserId(userId); // update userId as well
        post.setTitle(title);
        post.setContent(content);

        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file);
            post.setFilePath(filePath);
        }

        return PostDto.fromEntity(postRepository.save(post));
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.deleteById(id);
    }

    // 파일 저장
    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String uploadDir = "uploads/";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String filePath = uploadDir + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        return filePath;
    }
}
