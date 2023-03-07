package com.example.WriteHere.controller;

import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.user.User;
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
    public NotificationsController(
            UserService userService,
            NotificationService notificationService
    ) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @DeleteMapping("/{id}")
    public String pageOfDeleteNotification(
            @PathVariable Long id,
            Principal principal
    ) {
        if (principal != null) {
            notificationService.deleteById(id);
        }
        return "redirect:/profile/notifications";
    }
    @PatchMapping("/{id}/check")
    public String checkNotification(
            @PathVariable Long id,
            Principal principal
    ) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            Notification notification = notificationService.findById(id);
            if (user.getNotifications().contains(notification)) {
                notification.setCheckedStatus(true);
                notificationService.save(notification);
            }
        }
        return "redirect:/profile/notifications";
    }
    @PatchMapping("/check-all")
    public String checkAllNotification(Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            user.getNotifications().forEach(x -> x.setCheckedStatus(true));
            userService.saveAfterChange(user);
        }
        return "redirect:/profile/notifications";
    }
    @DeleteMapping("/delete-all")
    public String deleteAllNotification(Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            notificationService.deleteAllByUser(user);
        }
        return "redirect:/profile/notifications";
    }
}
