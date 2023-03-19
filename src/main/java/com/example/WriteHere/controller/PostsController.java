package com.example.WriteHere.controller;

import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.report.ReportByPost;
import com.example.WriteHere.model.user.Role;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.*;
import com.example.WriteHere.service.report.ReportPostService;
import com.example.WriteHere.service.user.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/posts")
public class PostsController {
    private final PostService postService;
    private final UserService userService;
    private final ReportPostService reportPostService;
    private final ConvertMethods convertMethods;
    private final CommentsService commentsService;
    private final NotificationService notificationService;
    private final ComparatorsTypes comparatorsTypes;
    @Autowired
    public PostsController(
            PostService postService,
            UserService userService,
            ReportPostService reportPostService,
            ConvertMethods convertMethods,
            CommentsService commentsService,
            NotificationService notificationService,
            ComparatorsTypes comparatorsTypes
    ) {
        this.postService = postService;
        this.userService = userService;
        this.reportPostService = reportPostService;
        this.convertMethods = convertMethods;
        this.commentsService = commentsService;
        this.notificationService = notificationService;
        this.comparatorsTypes = comparatorsTypes;
    }

    @GetMapping()
    public String pageOfAllPosts(
            @NonNull Model model,
            Principal principal
    ) {
        if (principal == null) {
            model.addAttribute("all_posts", comparatorsTypes.getSortedPostsByDateOfCreated(postService.findAll()));
        }
        else {
            User user = userService.findByEmail(principal.getName());
            List<Post> allPosts = new ArrayList<>(comparatorsTypes.getSortedPostsByDateOfCreated(postService.findAll()));
            allPosts.removeAll(user.getBlackListOfPosts());
            model.addAttribute("all_posts", allPosts);
            model.addAttribute("user", userService.findByEmail(principal.getName()));
            model.addAttribute("countOfNotifications", (int) user.getNotifications().stream().filter(notification -> !notification.getCheckedStatus()).count());
        }
        model.addAttribute("IsNotPageOfAllPosts", false);
        model.addAttribute("principal", principal);
        model.addAttribute("nameOfPage", "Posts");
        return "/posts/all_posts";
    }
    @GetMapping("/search")
    public String pageOfSearchPosts(
            @RequestParam("name") String name,
            Model model,
            Principal principal
    ) {
        model.addAttribute("principal", principal);
        model.addAttribute(
                "all_posts",
                postService.findByTitleOrText(name)
                        .stream()
                        .sorted(comparatorsTypes.getComparatorPostsByDateOfCreated())
                        .toList()
        );
        model.addAttribute("nameOfPage", "Search: " + name);
        model.addAttribute("name", name);
        return "/posts/all_posts";
    }
    @GetMapping("/{id}")
    public String pageOfSelectedPost(
            @PathVariable Long id,
            @NonNull Model model,
            Principal principal
    ) {
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        model.addAttribute("images", post.getImages());
        model.addAttribute("nameOfPage", post.getTitle());
        if (principal == null) {
            model.addAttribute("comments", comparatorsTypes.getSortedCommentsByDateOfCreated(post.getComments()));
        }
        model.addAttribute("principal", principal);
        model.addAttribute("comment", new Comment());
        model.addAttribute("report", new ReportByPost());
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (post.getUser() != null) {
                model.addAttribute("userIsOwner", post.getUser().equals(user));
            }
            model.addAttribute("createdReportIsExist", user.getBlackListOfPosts().contains(post));
            model.addAttribute("comments", commentsService.findComments(principal, post)
                    .stream()
                    .sorted(comparatorsTypes.getComparatorCommentsByDateOfCreated())
                    .toList()
            );
            model.addAttribute("user", user);
        }
        return "/posts/selected_post";
    }
    @GetMapping("/new")
    public String pageOfNewPost(
            @NonNull Model model,
            Principal principal
    ) {
        model.addAttribute("post", new Post());
        model.addAttribute("nameOfPage", "New post");
        model.addAttribute("principal", principal);
        return "/posts/new_post";
    }
    @PostMapping()
    public String saveNewPost(
            Principal principal,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @ModelAttribute Post post,
            BindingResult bindingResult
    ) {
        postService.setFieldsForPost(post, images);
        postService.setOwnerForPost(post, principal);
        postService.save(post);
        return "redirect:/posts";
    }
    @PatchMapping("/{id}")
    public String editPost(
            @PathVariable Long id,
            @ModelAttribute Post post,
            BindingResult bindingResult
    ) {
        Post postFromDB = postService.findById(id);
        if (bindingResult.hasErrors()) {
            return "redirect:/posts/{id}/edit";
        }
        postService.renewFieldsOfPost(postFromDB, post);
        return "redirect:/posts/{id}";
    }
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Post post = postService.findById(id);
        notificationService.createNotificationAboutDeleteComment(post, principal, user, id);
        if (user.getRole().equals(Role.ADMIN)) {
            return "redirect:/admin/posts";
        }
        return "redirect:/profile";
    }
    @PostMapping("{id}/add_comment")
    public String saveNewComment(
            @PathVariable Long id,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            Principal principal,
            @ModelAttribute Comment comment,
            BindingResult bindingResult
    ) {
        commentsService.setOwnerOfComment(comment, principal);
        Post post = postService.findById(id);
        commentsService.setFieldsForComment(comment, post, images);
        notificationService.createNotificationAboutAddComment(post, principal, comment);
        return "redirect:/posts/{id}";
    }
    @PatchMapping("/{id}/like")
    public String likedPost(@PathVariable Long id, Principal principal) {
        Post post = postService.findById(id);
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            notificationService.createNotificationsAboutLikePost(post, user);
            post.setNumberOfLikes(
                    convertMethods.changeRating(post, post.getNumberOfLikes(), user.getLikedPosts())
            );
            user.setLikedPosts(
                    postService.togglePostToTheSecondCollectionOfUser(user.getLikedPosts(), post, user)
            );
            if (user.getDislikedPosts().contains(post)) {
                user.getDislikedPosts().remove(post);
                post.setNumberOfDislikes(post.getNumberOfDislikes() - 1);
            }
            userService.saveAfterChange(user);
            postService.save(post);
            return "redirect:/posts/{id}";
        }
        return "redirect:/login";
    }
    @PatchMapping("/{id}/dislike")
    public String dislikedPost(@PathVariable Long id, Principal principal) {
        Post post = postService.findById(id);
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            notificationService.createNotificationsAboutDisLikePost(post, user);
            post.setNumberOfDislikes(
                    convertMethods.changeRating(post, post.getNumberOfDislikes(), user.getDislikedPosts())
            );
            user.setDislikedPosts(
                    postService.togglePostToTheSecondCollectionOfUser(user.getDislikedPosts(), post, user)
            );
            if (user.getLikedPosts().contains(post)) {
                user.getLikedPosts().remove(post);
                post.setNumberOfLikes(post.getNumberOfLikes() - 1);
            }
            userService.saveAfterChange(user);
            postService.save(post);
            return "redirect:/posts/{id}";
        }
        return "redirect:/login";
    }

    @PostMapping("/{id}/report")
    public String createNewReportForPost(
            @PathVariable Long id,
            Model model,
            Principal principal,
            @ModelAttribute("report") @Valid ReportByPost report,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/posts/{id}";
        }
        if (principal == null) {
            return "redirect:/posts/{id}";
        }
        User user = userService.findByEmail(principal.getName());
        Post post = postService.findById(id);

        if (!user.getBlackListOfPosts().contains(post)) {
            report.setId(null);
            report.setPost(postService.findById(id));
            reportPostService.save(report);
            user.getBlackListOfPosts().add(post);
        }
        model.addAttribute("typeOfElement", "post");
        model.addAttribute("element", post);
        userService.saveAfterChange(user);
        return "/success_report";
    }
}
