package com.sbb.ecars;

import com.sbb.ecars.domain.Post;
import com.sbb.ecars.dto.PostDto;
import com.sbb.ecars.repository.PostRepository;
import com.sbb.ecars.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .userId("testUser")
                .title("Test Title")
                .content("This is test")
                .filePath("uploads/test.jpg")
                .build();

        postDto = PostDto.fromEntity(post);
    }

    @Test
    void testGetAllPosts() {
        when(postRepository.findAll()).thenReturn(List.of(post));

        List<PostDto> posts = postService.getAllPosts();

        assertEquals(1, posts.size());
        assertEquals("Test Title", posts.get(0).getTitle());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void testGetPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostDto foundPost = postService.getPostById(1L);

        assertNotNull(foundPost);
        assertEquals("Test Title", foundPost.getTitle());
        verify(postRepository, times(1)).findById(1L);
    }

    // CreatePost
    /*@Test
    void testCreatePost() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String mockFilePath = "uploads/test.jpg";
        PostService spyPostService = spy(postService);
        doReturn(mockFilePath).when(spyPostService).saveFile(file);

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            savedPost.setId(1L);
            return savedPost;
        });

        PostDto createdPost = spyPostService.createPost("testUser", "Test Title", "Test Content", file);

        assertNotNull(createdPost);
        assertEquals("Test Title", createdPost.getTitle());
        assertEquals("Test Content", createdPost.getContent());
        assertEquals("testUser", createdPost.getUserId());
        assertEquals(mockFilePath, createdPost.getFilePath());

        verify(spyPostService, times(1)).saveFile(file);
        verify(postRepository, times(1)).save(any(Post.class));
    }
     */

    @Test
    void testCreatePostWithoutFile() throws IOException {
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            savedPost.setId(2L);
            return savedPost;
        });

        PostDto createdPost = postService.createPost("testUser", "Test Title", "Test Content", null);

        assertNotNull(createdPost);
        assertEquals("Test Title", createdPost.getTitle());
        assertEquals("Test Content", createdPost.getContent());
        assertEquals("testUser", createdPost.getUserId());
        assertNull(createdPost.getFilePath());

        verify(postRepository, times(1)).save(any(Post.class));
    }

    // Update Post
    /*
    @Test
    void testUpdatePost() throws IOException {
        Post existingPost = Post.builder()
                .id(1L)
                .userId("testUser")
                .title("Old Title")
                .content("Old Content")
                .filePath("uploads/old.jpg")
                .build();

        String updatedTitle = "Updated Title";
        String updatedContent = "Updated Content";
        MockMultipartFile updatedFile = new MockMultipartFile(
                "file",
                "updated.jpg",
                "image/jpeg",
                "updated image content".getBytes()
        );

        String updatedFilePath = "uploads/updated.jpg";

        PostService spyPostService = spy(postService);
        doReturn(updatedFilePath).when(spyPostService).saveFile(updatedFile);

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            savedPost.setId(1L);
            return savedPost;
        });

        PostDto updatedPost = spyPostService.updatePost(1L, updatedTitle, updatedContent, updatedFile);

        assertNotNull(updatedPost);
        assertEquals(updatedTitle, updatedPost.getTitle());
        assertEquals(updatedContent, updatedPost.getContent());
        assertEquals(updatedFilePath, updatedPost.getFilePath());

        verify(spyPostService, times(1)).saveFile(updatedFile);
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }
     */

    @Test
    void testDeletePost() {
        Post existingPost = Post.builder()
                .id(1L)
                .userId("testUser")
                .title("To be deleted")
                .content("This post will be deleted")
                .filePath("uploads/delete.jpg")
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        doNothing().when(postRepository).deleteById(1L);

        postService.deletePost(1L);

        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }
}
