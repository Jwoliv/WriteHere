package com.example.WriteHere.controller;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.service.PostService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;

@Controller
@RequestMapping("/posts")
public class PostsController {
    private final PostService postService;
    @Autowired
    public PostsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    public String pageOfAllPosts(
            @NonNull Model model,
            Principal principal
    ) {
        model.addAttribute("posts", postService.findAll());
        model.addAttribute("principal", principal);
        model.addAttribute("nameOfPage", "Posts");
        return "/posts/all_posts";
    }
    @GetMapping("/{id}")
    public String pageOfSelectedPost(
            @PathVariable Long id,
            @NonNull Model model,
            Principal principal
    ) {
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        model.addAttribute("nameOfPage", post.getTitle());
        model.addAttribute("principal", principal);
        return "/posts/selected_post";
    }
    @GetMapping("/new")
    public String pageOfNewPost(
            @NonNull Model model,
            Principal principal
    ) {
        model.addAttribute("post", new Post());
        model.addAttribute("nameOfPage", "New post");
        model.addAttribute("principal", principal);
        return "/posts/new_post";
    }
    @GetMapping("/{id}/edit")
    public String pageOfEditedPost(
            @PathVariable Long id,
            @NonNull Model model,
            Principal principal
    ) {
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("principal", principal);
        return "/posts/edit_post";
    }
    @PostMapping()
    public String saveNewPost(
            @ModelAttribute @Valid Post post,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/posts/new";
        }
        post.setDateOfCreated(new Date());
        post.setNumberOfLikes(0);
        post.setNumberOfDislikes(0);
        postService.save(post);
        return "redirect:/posts";
    }
    @PatchMapping("/{id}")
    public String editPost(
            @ModelAttribute @Valid Post post,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/posts/{id}/edit";
        }
        postService.save(post);
        return "redirect:/posts/{id}";
    }
}
