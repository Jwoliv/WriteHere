package com.example.WriteHere.controller;

import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.user.User;
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
        return "/profile/notifications";
    }
}
