package com.example.WriteHere.controller;

import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.report.ReportByComment;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.user.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String pageOfProfile(@NonNull Model model, Principal principal) {
        model.addAttribute("principal", principal);
        model.addAttribute("user", userService.findByEmail(principal.getName()));
        return "/profile/profile";
    }
    @GetMapping("/notifications")
    public String pageOfNotificationOfUser(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Notification> notifications = user.getNotifications().stream().sorted(
                Comparator.comparing(Notification::getDateOfCreated
                ).reversed()).toList();

        model.addAttribute("principal", principal);
        model.addAttribute("notifications", notifications);
        model.addAttribute("hasAnyAreNotChecked",
                notifications.stream().anyMatch(notification -> !notification.getCheckedStatus())
        );
        model.addAttribute("user", user);
        model.addAttribute("url", "profile");
        return "/profile/notifications";
    }
    @GetMapping("/posts")
    public String pageOfPostOfSelectedUsers(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("nameOfPage", "Posts");
        model.addAttribute("principal", principal);
        model.addAttribute("all_posts", user.getPosts()
                .stream()
                .sorted(Comparator.comparing(Post::getDateOfCreated).reversed())
                .toList()
        );
        model.addAttribute("IsNotPageOfAllPosts", true);
        return "posts/all_posts";
    }
    @GetMapping("/liked-posts")
    public String pageOfLikedPostOfSelectedUsers(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("nameOfPage", "Liked posts");
        model.addAttribute("principal", principal);
        model.addAttribute("all_posts", user.getLikedPosts()
                .stream()
                .sorted(Comparator.comparing(Post::getDateOfCreated).reversed())
                .toList()
        );
        model.addAttribute("IsNotPageOfAllPosts", true);
        return "posts/all_posts";
    }
    @GetMapping("/comments")
    public String pageOfCommentsOfSelectedUsers(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("nameOfPage", "Comments");
        model.addAttribute("principal", principal);
        model.addAttribute("all_comments", user.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getDateOfCreated).reversed())
                .toList()
        );
        model.addAttribute("user", user);
        model.addAttribute("report", new ReportByComment());
        return "user/commentsByUser";
    }
    @GetMapping("/liked-comments")
    public String pageOfLikedCommentsOfSelectedUsers(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("nameOfPage", "Liked comments");
        model.addAttribute("principal", principal);
        model.addAttribute("all_comments", user.getLikedComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getDateOfCreated).reversed())
                .toList()
        );
        model.addAttribute("user", user);
        model.addAttribute("report", new ReportByComment());
        return "user/commentsByUser";
    }
}
