package com.example.WriteHere.controller;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final PostService postService;
    @Autowired
    public AdminController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String pageOfMainAdmin(Model model, Principal principal) {
        model.addAttribute("principal", principal);
        return "/admin/main_admin";
    }
    @GetMapping("/posts")
    public String pageOfPosts(Model model, Principal principal) {
        model.addAttribute("principal", principal);
        model.addAttribute("all_posts", postService.findAll()
                .stream()
                .sorted(Comparator.comparing(Post::getNumberOfLikes))
                .collect(Collectors.toList())
        );
        model.addAttribute("IsNotPageOfAllPosts", false);
        return "/admin/posts/all_posts";
    }
    @GetMapping("/posts/{id}")
    public String pageOfSelectedPost(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("principal", principal);
        return "/admin/posts/selected_post";
    }
}
