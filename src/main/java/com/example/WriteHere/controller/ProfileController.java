package com.example.WriteHere.controller;

import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.report.ReportByComment;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.ComparatorsTypes;
import com.example.WriteHere.service.user.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final ComparatorsTypes comparatorsTypes;

    @Autowired
    public ProfileController(UserService userService, ComparatorsTypes comparatorsTypes) {
        this.userService = userService;
        this.comparatorsTypes = comparatorsTypes;
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
        model.addAttribute("all_posts",
                comparatorsTypes.getSortedPostsByDateOfCreated(user.getPosts())
        );
        model.addAttribute("IsNotPageOfAllPosts", true);
        return "posts/all_posts";
    }
    @GetMapping("/liked-posts")
    public String pageOfLikedPostOfSelectedUsers(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("nameOfPage", "Liked posts");
        model.addAttribute("principal", principal);
        model.addAttribute("all_posts",
                comparatorsTypes.getSortedPostsByDateOfCreated(user.getLikedPosts())
        );
        model.addAttribute("IsNotPageOfAllPosts", true);
        return "posts/all_posts";
    }
    @GetMapping("/comments")
    public String pageOfCommentsOfSelectedUsers(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("nameOfPage", "Comments");
        model.addAttribute("principal", principal);
        model.addAttribute("all_comments",
                comparatorsTypes.getSortedCommentsByDateOfCreated(user.getComments())
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
        model.addAttribute("all_comments",
                comparatorsTypes.getSortedCommentsByDateOfCreated(user.getLikedComments())
        );
        model.addAttribute("user", user);
        model.addAttribute("report", new ReportByComment());
        return "user/commentsByUser";
    }
}
