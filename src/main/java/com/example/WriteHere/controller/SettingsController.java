package com.example.WriteHere.controller;

import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/settings")
public class SettingsController {
    private final UserService userService;

    @Autowired
    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String pageOfSettings(Principal principal, Model model) {
        model.addAttribute("principal", principal);
        model.addAttribute("user", userService.findByEmail(principal.getName()));
        return "profile/settings";
    }
    @PatchMapping("/set-private")
    public String pageOfSetPrivate(
            @RequestParam("isPrivate") Boolean isPrivate,
            Principal principal
    ) {
        User user = userService.findByEmail(principal.getName());
        if (isPrivate != null) {
            user.setIsPrivate(isPrivate);
            userService.saveAfterChange(user);
        }
        return "redirect:/profile";
    }
    @PatchMapping("/change-names")
    public String pageOfChangeNames(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            Principal principal
    ) {
        User user = userService.findByEmail(principal.getName());
        if (firstname != null && lastname != null) {
            user.setFirstname(firstname);
            user.setLastname(lastname);
            userService.saveAfterChange(user);
        }
        return "redirect:/profile";
    }
    @DeleteMapping("/delete-account")
    public String pageOfDeleteAccount(Principal principal) {
        userService.deleteByEmail(principal.getName());
        return "redirect:/login";
    }
}
