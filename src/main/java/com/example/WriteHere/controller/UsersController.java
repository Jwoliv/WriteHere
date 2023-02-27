package com.example.WriteHere.controller;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.PostService;
import com.example.WriteHere.service.user.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.Locale;

@Controller
@RequestMapping(("/users"))
public class UsersController {
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public UsersController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("all_users", userService.findAll());
        model.addAttribute("nameOfPage", "Users");
        return "user/allUsers";
    }
    @GetMapping("/search")
    public String pageOfUsersWithSameName(@RequestParam("name") String name, Model model) {
        model.addAttribute("all_users", userService.findByName(name));
        model.addAttribute("nameOfPage", name);
        return "user/allUsers";
    }
    @GetMapping("/{id}")
    public String selectedUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("all_posts",
                user.getPosts().stream()
                        .sorted(Comparator.comparing(Post::getDateOfCreated).reversed()).toList()
        );
        return "user/selectedUser";
    }
    @GetMapping("/{id}/search")
    public String searchPostsOfUserByTitleOrText(@PathVariable Long id, @RequestParam("name") String name, @NonNull Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("all_posts",
                postService.findByTitleOrTextAndUserId(name.toUpperCase(Locale.ROOT), user.getId())
                        .stream()
                        .sorted(Comparator.comparing(Post::getDateOfCreated).reversed()).toList()
        );
        model.addAttribute("name", name);
        return "/user/selectedUser";
    }
}