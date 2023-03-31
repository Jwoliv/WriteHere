package com.example.WriteHere.controller;

import com.example.WriteHere.service.NotificationService;
import com.example.WriteHere.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/notifications")
public class NotificationsController {
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationsController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @DeleteMapping("/{id}")
    public String pageOfDeleteNotification(@PathVariable Long id, Principal principal) {
        notificationService.deleteById(id, principal);
        return "redirect:/profile/notifications";
    }
    @PatchMapping("/{id}/check")
    public String checkNotification(@PathVariable Long id, Principal principal) {
        notificationService.checkNotificationById(id, principal);
        return "redirect:/profile/notifications";
    }
    @PatchMapping("/check-all")
    public String checkAllNotification(Principal principal) {
        userService.markAllNotificationsAsCheckedOfUser(principal);
        return "redirect:/profile/notifications";
    }
    @DeleteMapping("/delete-all")
    public String deleteAllNotification(Principal principal) {
        notificationService.deleteAllByUser(principal);
        return "redirect:/profile/notifications";
    }
}
