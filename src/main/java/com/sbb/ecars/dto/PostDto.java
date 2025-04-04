package com.sbb.ecars.dto;

import com.sbb.ecars.domain.Post;
import lombok.*;

import java.util.HashMap;
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
        Map<String, Object> fields = new HashMap<>();
        fields.put("user_id", post.getUserId());
        fields.put("title", post.getTitle());
        fields.put("content", post.getContent());
        fields.put("created_at", post.getCreatedAt());
        fields.put("file_path", post.getFilePath());

        return PostDto.builder()
                .pk(post.getId())
                .fields(fields)
                .build();
    }
}
