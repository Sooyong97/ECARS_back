package com.sbb.ecars.dto;

import com.sbb.ecars.domain.Post;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long pk;
    private Map<String, Object> fields;

    public static PostDto fromEntity(Post post) {
        return PostDto.builder()
                .pk(post.getId())
                .fields(Map.of(
                    "user_id", post.getUserId(),
                    "title", post.getTitle(),
                    "content", post.getContent(),
                    "created_at", post.getCreatedAt(),
                    "file_path", post.getFilePath()
                ))
                .build();
    }
}
