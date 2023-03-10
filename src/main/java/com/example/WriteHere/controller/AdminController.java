package com.example.WriteHere.controller;

import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.ComparatorsTypes;
import com.example.WriteHere.service.PostService;
import com.example.WriteHere.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final PostService postService;
    private final UserService userService;
    private final ComparatorsTypes comparatorsTypes;
    @Autowired
    public AdminController(PostService postService, UserService userService, ComparatorsTypes comparatorsTypes) {
        this.postService = postService;
        this.userService = userService;
        this.comparatorsTypes = comparatorsTypes;
    }

    @GetMapping
    public String pageOfMainAdmin(Model model, Principal principal) {
        model.addAttribute("principal", principal);
        return "/admin/main_admin";
    }
    @GetMapping("/posts")
    public String pageOfPosts(Model model, Principal principal) {
        model.addAttribute("principal", principal);
        model.addAttribute("all_elements",
                comparatorsTypes.getSortedPostsByDateOfCreated(postService.findAll())
        );
        model.addAttribute("IsNotPageOfAllPosts", false);
        model.addAttribute("url", "posts");
        return "/admin/element/all_elements";
    }
    @GetMapping("/posts/search")
    public String pageOfSearchPosts(
            @RequestParam("name") String name,
            Model model,
            Principal principal
    ) {
        model.addAttribute("all_elements",
                comparatorsTypes.getSortedPostsByDateOfCreated(postService.findByTitleOrText(name))
        );
        model.addAttribute("principal", principal);
        model.addAttribute("IsNotPageOfAllPosts", false);
        model.addAttribute("url", "posts");
        model.addAttribute("name", name);
        return "/admin/element/all_elements";
    }
    @GetMapping("/posts/{id}")
    public String pageOfSelectedPost(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("principal", principal);
        model.addAttribute("url", "posts");
        return "/admin/element/selected_element";
    }
    @GetMapping("/posts/{id}/comments")
    public String pageOfCommentsByPost(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("all_elements",
                comparatorsTypes.getSortedCommentsByDateOfCreated(
                        postService.findById(id).getComments()
                )
        );
        model.addAttribute("principal", principal);
        model.addAttribute("url", "comments");
        model.addAttribute("IsNotPageOfAllPosts", false);
        return "/admin/element/all_elements";
    }
    @GetMapping("/users")
    public String pageOfUsers(Model model, Principal principal) {
        model.addAttribute("users", userService.findAll()
                .stream()
                .sorted(Comparator
                        .comparing(User::getNumberOfReports)
                        .reversed()
                ).collect(Collectors.toList())
        );
        model.addAttribute("principal", principal);
        return "/admin/user/all_users";
    }
}
