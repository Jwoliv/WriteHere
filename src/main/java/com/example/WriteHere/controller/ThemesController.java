package com.example.WriteHere.controller;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.post.Theme;
import com.example.WriteHere.service.PostService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Comparator;

@Controller
@RequestMapping("/theme")
public class ThemesController {
    private final PostService postService;

    @Autowired
    public ThemesController(PostService postService) {
        this.postService = postService;
    }
    @GetMapping
    public String pageOfAllThemes(
            @NonNull Model model,
            Principal principal
    ) {
        model.addAttribute("nameOfPage", "Themes");
        model.addAttribute("principal", principal);
        return "/theme/all_themes";
    }
    @GetMapping("/{theme}")
    public String pageOfPostsByTheme(
            @PathVariable Theme theme,
            @NonNull Model model,
            Principal principal
    ) {
        model.addAttribute("nameOfPage", theme.getDisplayName());
        model.addAttribute("principal", principal);
        model.addAttribute("all_posts", postService.findByTheme(theme).stream().sorted(
                Comparator.comparing(Post::getDateOfCreated).reversed()
        ).toList());
        return "/posts/all_posts";
    }
}
