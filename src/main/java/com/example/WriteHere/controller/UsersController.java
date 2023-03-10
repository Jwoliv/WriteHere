package com.example.WriteHere.controller;

import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.report.ReportByComment;
import com.example.WriteHere.model.user.Role;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.ComparatorsTypes;
import com.example.WriteHere.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping(("/users"))
public class UsersController {
    private final UserService userService;
    private final ComparatorsTypes comparatorsTypes;

    @Autowired
    public UsersController(UserService userService, ComparatorsTypes comparatorsTypes) {
        this.userService = userService;
        this.comparatorsTypes = comparatorsTypes;
    }

    @GetMapping
    public String index(Model model, Principal principal) {
        model.addAttribute("all_users", userService.findAll()
                .stream()
                .sorted(Comparator.comparingInt((User x) -> x.getPosts().size()).reversed())
                .collect(Collectors.toList())
        );
        model.addAttribute("nameOfPage", "Users");
        model.addAttribute("principal", principal);
        return "user/allUsers";
    }
    @GetMapping("/search")
    public String pageOfUsersWithSameName(@RequestParam("name") String name, Model model) {
        model.addAttribute("all_users", userService.findByName(name));
        model.addAttribute("nameOfPage", "Search: " + name);
        model.addAttribute("name", name);
        return "user/allUsers";
    }
    @GetMapping("/{id}")
    public String selectedUser(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("all_posts",
                user.getPosts().stream()
                        .sorted(Comparator.comparing(Post::getDateOfCreated).reversed()).toList()
        );
        model.addAttribute("principal", principal);
        return "user/selectedUser";
    }
    @DeleteMapping("/{id}")
    public String pageOfDeletedUser(@PathVariable Long id, Principal principal) {
        User user = userService.findById(id);
        User userOfSession = userService.findByEmail(principal.getName());
        if (userOfSession.getRole().equals(Role.ADMIN) && !user.getRole().equals(Role.ADMIN)) {
            userService.deleteById(id);
        }
        return "redirect:/admin/users";
    }
    @GetMapping("/{id}/posts")
    public String pageOfPostOfSelectedUsers(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findById(id);
        User userOfSession = userService.findByEmail(principal.getName());
        if (user.getIsPrivate() && !userOfSession.getRole().equals(Role.ADMIN)) {
            return "redirect:/users/{id}";
        }
        model.addAttribute("nameOfPage", "Posts by " + user.getFullName());
        model.addAttribute("principal", principal);
        model.addAttribute("all_posts",
                comparatorsTypes.getSortedPostsByDateOfCreated(user.getPosts())
        );
        model.addAttribute("IsNotPageOfAllPosts", true);
        return "posts/all_posts";
    }
    @GetMapping("/{id}/liked-posts")
    public String pageOfLikedPostOfSelectedUsers(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findById(id);
        User userOfSession = userService.findByEmail(principal.getName());
        if (user.getIsPrivate() && !userOfSession.getRole().equals(Role.ADMIN)) {
            return "redirect:/users/{id}";
        }
        model.addAttribute("nameOfPage", "Liked posts by " + user.getFullName());
        model.addAttribute("principal", principal);
        model.addAttribute("all_posts",
                comparatorsTypes.getSortedPostsByDateOfCreated(user.getLikedPosts())
        );
        model.addAttribute("IsNotPageOfAllPosts", true);
        return "posts/all_posts";
    }
    @GetMapping("/{id}/comments")
    public String pageOfCommentsOfSelectedUsers(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findById(id);
        User userOfSession = userService.findByEmail(principal.getName());
        if (user.getIsPrivate() && !userOfSession.getRole().equals(Role.ADMIN)) {
            return "redirect:/users/{id}";
        }
        model.addAttribute("nameOfPage", "Comments by " + user.getFullName());
        model.addAttribute("principal", principal);
        model.addAttribute("all_comments",
                comparatorsTypes.getSortedCommentsByDateOfCreated(user.getComments())
        );
        model.addAttribute("user", userService.findByEmail(principal.getName()));
        model.addAttribute("report", new ReportByComment());
        return "user/commentsByUser";
    }
    @GetMapping("/{id}/liked-comments")
    public String pageOfLikedCommentsOfSelectedUsers(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findById(id);
        User userOfSession = userService.findByEmail(principal.getName());
        if (user.getIsPrivate() && !userOfSession.getRole().equals(Role.ADMIN)) {
            return "redirect:/users/{id}";
        }
        model.addAttribute("nameOfPage", "Liked comments by " + user.getFullName());
        model.addAttribute("principal", principal);
        model.addAttribute("all_comments", user.getLikedComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getDateOfCreated).reversed())
                .toList()
        );
        model.addAttribute("user", userService.findByEmail(principal.getName()));
        model.addAttribute("report", new ReportByComment());
        return "user/commentsByUser";
    }
}