package com.example.WriteHere.controller;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.PostService;
import com.example.WriteHere.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PostsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @Test
    public void pageOfSelectedPost() {
        Long postId = 1L;
        Post testPost = new Post();
        testPost.setId(postId);
        testPost.setTitle("Title example");
        testPost.setText("Text example");

        given(postService.findById(postId)).willReturn(testPost);

        try {
            mockMvc.perform(get("/posts/{id}", postId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("/posts/selected_post"))
                    .andExpect(model().attribute("post", testPost))
                    .andExpect(model().attributeExists("images"))
                    .andExpect(model().attribute("nameOfPage", testPost.getTitle()))
                    .andExpect(model().attributeExists("comment"))
                    .andExpect(model().attributeExists("report"))
                    .andExpect(model().attributeExists("comments"))
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void setLikeForPost() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setNumberOfLikes(0);
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFullName("John Doe");
        user.setLikedPosts(new ArrayList<>());
        user.setDislikedPosts(new ArrayList<>());

        when(postService.findById(postId)).thenReturn(post);
        when(userService.findByEmail("test@test.com")).thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null)
        );

        try {
            mockMvc.perform(get("/posts/{id}", postId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("/posts/selected_post"))
                    .andExpect(model().attribute("post", post))
                    .andExpect(model().attributeExists("images"))
                    .andExpect(model().attribute("nameOfPage", post.getTitle()))
                    .andExpect(model().attributeExists("comment"))
                    .andExpect(model().attributeExists("report"))
                    .andExpect(model().attributeExists("comments"))
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SecurityContextHolder.clearContext();
    }
    @Test
    public void setDislikeForPost() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setNumberOfLikes(0);
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFullName("John Doe");
        user.setLikedPosts(new ArrayList<>());
        user.setDislikedPosts(new ArrayList<>());

        when(postService.findById(postId)).thenReturn(post);
        when(userService.findByEmail("test@test.com")).thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null)
        );

        try {
            mockMvc.perform(get("/posts/{id}", postId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("/posts/selected_post"))
                    .andExpect(model().attribute("post", post))
                    .andExpect(model().attributeExists("images"))
                    .andExpect(model().attribute("nameOfPage", post.getTitle()))
                    .andExpect(model().attributeExists("comment"))
                    .andExpect(model().attributeExists("report"))
                    .andExpect(model().attributeExists("comments"))
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SecurityContextHolder.clearContext();
    }
}