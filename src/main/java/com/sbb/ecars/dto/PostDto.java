package com.sbb.ecars.dto;

import com.sbb.ecars.domain.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String filePath;

    public static PostDto fromEntity(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .filePath(post.getFilePath())
                .build();
    }

}
