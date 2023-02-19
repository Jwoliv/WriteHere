package com.example.WriteHere.controller;

import com.example.WriteHere.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(("/users"))
public class UsersController {
    private final UserService userService;


    public UsersController(UserService userService) {
        this.userService = userService;
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
        model.addAttribute("user", userService.findById(id));
        return "user/selectedUser";
    }
}