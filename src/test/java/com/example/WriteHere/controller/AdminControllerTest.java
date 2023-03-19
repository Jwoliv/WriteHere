package com.example.WriteHere.controller;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.service.ComparatorsTypes;
import com.example.WriteHere.service.PostService;
import com.example.WriteHere.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private PostService postService;

    @Mock
    public UserService userService;

    @Mock
    public ComparatorsTypes comparatorsTypes;
    @Mock
    private Model model;
    @Mock
    private Principal principal;

    @Test
    public void testPageOfMainAdmin() {
        AdminController adminController = new AdminController(postService, userService, comparatorsTypes);
        String result = adminController.pageOfMainAdmin(model, principal);
        assertEquals("/admin/main_admin", result);
    }

    @Test
    public void testPageOfPosts() {
        AdminController adminController = new AdminController(postService, userService, comparatorsTypes);
        List<Post> posts = new ArrayList<>();
        when(postService.findAll()).thenReturn(posts);
        when(comparatorsTypes.getSortedPostsByDateOfCreated(posts)).thenReturn(posts);
        String result = adminController.pageOfPosts(model, principal);
        assertEquals("/admin/element/all_elements", result);
        verify(model).addAttribute(eq("principal"), eq(principal));
        verify(model).addAttribute(eq("all_elements"), eq(posts));
        verify(model).addAttribute(eq("IsNotPageOfAllPosts"), eq(false));
        verify(model).addAttribute(eq("url"), eq("posts"));
    }

    @Test
    public void testPageOfSearchPosts() {
        AdminController adminController = new AdminController(postService, userService, comparatorsTypes);
        String name = "searchName";
        List<Post> posts = new ArrayList<>();
        when(postService.findByTitleOrText(name)).thenReturn(posts);
        when(comparatorsTypes.getSortedPostsByDateOfCreated(posts)).thenReturn(posts);
        String result = adminController.pageOfSearchPosts(name, model, principal);
        assertEquals("/admin/element/all_elements", result);
        verify(model).addAttribute(eq("principal"), eq(principal));
        verify(model).addAttribute(eq("all_elements"), eq(posts));
        verify(model).addAttribute(eq("IsNotPageOfAllPosts"), eq(false));
        verify(model).addAttribute(eq("url"), eq("posts"));
        verify(model).addAttribute(eq("name"), eq(name));
    }

    @Test
    public void testPageOfSelectedPost() {
        AdminController adminController = new AdminController(postService, userService, comparatorsTypes);
        Long id = 1L;
        Post post = new Post();
        when(postService.findById(id)).thenReturn(post);
        String result = adminController.pageOfSelectedPost(id, model, principal);
        assertEquals("/admin/element/selected_element", result);
        verify(model).addAttribute(eq("principal"), eq(principal));
        verify(model).addAttribute(eq("post"), eq(post));
        verify(model).addAttribute(eq("url"), eq("posts"));
    }
}