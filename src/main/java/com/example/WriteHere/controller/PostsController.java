package com.example.WriteHere.controller;

import com.example.WriteHere.model.image.ImageComment;
import com.example.WriteHere.model.image.ImagePost;
import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.notification.TypeOfNotification;
import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.report.ReportByPost;
import com.example.WriteHere.model.user.Role;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.CommentsService;
import com.example.WriteHere.service.PostService;
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

    @Autowired
    public PostsController(
            PostService postService,
            UserService userService,
            ReportPostService reportPostService,
            ConvertMethods convertMethods,
            CommentsService commentsService
    ) {
        this.postService = postService;
        this.userService = userService;
        this.reportPostService = reportPostService;
        this.convertMethods = convertMethods;
        this.commentsService = commentsService;
    }

    @GetMapping()
    public String pageOfAllPosts(
            @NonNull Model model,
            Principal principal
    ) {
        if (principal == null) {
            model.addAttribute("all_posts", postService.findAll().stream().sorted(
                    Comparator.comparing(Post::getDateOfCreated).reversed()
            ));
        }
        else {
            User user = userService.findByEmail(principal.getName());
            List<Post> allPosts = new ArrayList<>(postService.findAll().stream().sorted(
                    Comparator.comparing(Post::getDateOfCreated).reversed()
            ).toList());
            allPosts.removeAll(user.getBlackListOfPosts());
            model.addAttribute("all_posts", allPosts);
            model.addAttribute("user", userService.findByEmail(principal.getName()));
            model.addAttribute("countOfNotifications", (int) user.getNotifications().stream().filter(notification -> !notification.getCheckedStatus()).count());
        }
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
                        .sorted(Comparator.comparing(Post::getDateOfCreated).reversed())
                        .toList()
        );
        model.addAttribute("nameOfPage", name);
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
            model.addAttribute("comments", post.getComments().stream().sorted(
                    Comparator.comparing(Comment::getDateOfCreated).reversed()
            ).toList());
        }
        model.addAttribute("principal", principal);
        model.addAttribute("comment", new Comment());
        model.addAttribute("report", new ReportByPost());
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (post.getUser() != null) {
                model.addAttribute("userIsOwner", post.getUser().equals(user));
            }
            model.addAttribute("createdReportIsExist", !user.getBlackListOfPosts().contains(post));
            model.addAttribute("comments", findComments(principal, post).stream().sorted(
                    Comparator.comparing(Comment::getDateOfCreated).reversed()
            ).toList());
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
        post.setDateOfCreated(new Date());
        post.setNumberOfLikes(0);
        post.setNumberOfDislikes(0);
        post.setText(convertMethods.convertTextToMarkDown(post.getText()));
        post.setIsSuspicious(false);

        if (Arrays.stream(images).anyMatch(x -> !x.isEmpty())) {
            boolean isPrevious = true;
            for (MultipartFile multipartFile : images) {
                if (!multipartFile.isEmpty()) {
                    convertMethods.setImagesToList(multipartFile, post.getImages(), new ImagePost(), isPrevious);
                }
                isPrevious = false;
            }
            post.getImages().forEach(x -> x.setElement(post));
            postService.save(post);
            post.setPreviousId(post.getImages().get(0).getId());
        }

        if (principal == null) post.setIsByAnonymous(true);
        else {
            User user = userService.findByEmail(principal.getName());
            user.getPosts().add(post);
            post.setIsByAnonymous(false);
            post.setUser(user);
        }
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
        postFromDB.setTitle(post.getTitle());
        postFromDB.setText(post.getText());
        postFromDB.setTheme(post.getTheme());
        postFromDB.setIsSuspicious(false);
        postService.save(postFromDB);
        return "redirect:/posts/{id}";
    }
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Post post = postService.findById(id);

        if (user.getRole().equals(Role.ADMIN) || post.getUser() != null && post.getUser().equals(user)) {
            User userOfTheSession = userService.findByEmail(principal.getName());
            if (user.getRole().equals(Role.ADMIN) && post.getUser() != null && userOfTheSession != post.getUser()) {
                Notification notification = new Notification();
                notification.setTitle("Your posts is deleted");
                notification.setText(
                        "Your posts with name: " + post.getTitle() +
                        " in the theme: " + post.getTheme().getDisplayName() + " was deleted by admin"
                );
                setTheSameFieldsForNotification(notification, post, TypeOfNotification.PostIsDeleted);
            }
            postService.deleteById(id);
            userService.saveAfterChange(user);
            if (user.getRole().equals(Role.ADMIN)) return "redirect:/admin/posts";
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
        comment.setId(null);
        if (principal == null) {
            comment.setIsByAnonymous(true);
        }
        else {
            User user = userService.findByEmail(principal.getName());
            user.getComments().add(comment);
            comment.setIsByAnonymous(false);
            comment.setUser(user);
        }
        Post post = postService.findById(id);
        comment.setPost(post);
        post.getComments().add(comment);
        comment.setNumberOfDislikes(0);
        comment.setNumberOfLikes(0);
        comment.setDateOfCreated(new Date());
        comment.setText(convertMethods.convertTextToMarkDown(comment.getText()));
        comment.setIsSuspicious(false);
        if (Arrays.stream(images).anyMatch(x -> !x.isEmpty())) {
            boolean flag = true;
            for (MultipartFile multipartFile : images) {
                if (!multipartFile.isEmpty()) {
                    convertMethods.setImagesToList(multipartFile, comment.getImages(), new ImageComment(), flag);
                }
                flag = false;
            }
            comment.getImages().forEach(x -> x.setElement(comment));
        }
        if (post.getUser() != null) {
            Notification notification = new Notification();
            notification.setTitle("New comment of post");
            if (principal != null) {
                User authorOfComment = userService.findByEmail(principal.getName());
                notification.setText(
                        "Your posts with name: " + post.getTitle() +
                        " in the theme: " + post.getTheme().getDisplayName() +
                        " has a new comment, author of this comment is: " +
                        authorOfComment.getFullName()
                );
            } else {
                notification.setText(
                        "Your posts with name: " + post.getTitle() +
                        " in the theme: " + post.getTheme().getDisplayName() +
                        " has a new comment, author of this comment is Anonymous"
                );
            }
            setTheSameFieldsForNotification(notification, post, TypeOfNotification.NewComment);
        }
        commentsService.save(comment);
        return "redirect:/posts/{id}";
    }
    @PatchMapping("/{id}/like")
    public String likedPost(@PathVariable Long id, Principal principal) {
        Post post = postService.findById(id);
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getLikedPosts().contains(post) && post.getUser() != null
                    && !user.getId().equals(post.getUser().getId())
            ) {
                Notification notification = new Notification();
                notification.setTitle("Your posts is liked");
                notification.setText(
                        "Your posts with name: " + post.getTitle() +
                                " in the theme: " + post.getTheme().getDisplayName() +
                                " was liked by " + user.getFullName()
                );
                setTheSameFieldsForNotification(notification, post, TypeOfNotification.LikePost);
            }
            post.setNumberOfLikes(
                    convertMethods.changeRating(post, post.getNumberOfLikes(), user.getLikedPosts())
            );
            user.setLikedPosts(
                    togglePostToTheSecondCollectionOfUser(user.getLikedPosts(), post, user)
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
            if (!user.getDislikedPosts().contains(post) && post.getUser() != null
                    && !user.getId().equals(post.getUser().getId())
            ) {
               Notification notification = new Notification();
                notification.setTitle("Your posts is disliked");
                notification.setText(
                        "Your posts with name: " + post.getTitle() +
                                " in the theme: " + post.getTheme().getDisplayName() +
                                " was disliked by " + user.getFullName()
                );
                setTheSameFieldsForNotification(notification, post, TypeOfNotification.DislikePost);
            }
            post.setNumberOfDislikes(
                    convertMethods.changeRating(post, post.getNumberOfDislikes(), user.getDislikedPosts())
            );
            user.setDislikedPosts(
                    togglePostToTheSecondCollectionOfUser(user.getDislikedPosts(), post, user)
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

        userService.saveAfterChange(user);
        return "/success_report";
    }
    public List<Post> togglePostToTheSecondCollectionOfUser(List<Post> collection, Post post, User user) {
        if (collection.contains(post)) {
            collection.remove(post);
            post.getUsersWhoDislike().remove(user);
        }
        else {
            collection.add(post);
            post.getUsersWhoDislike().add(user);
        }
        return collection;
    }
    public List<Comment> findComments(Principal principal, Post post) {
        List<Comment> comments;
        if (principal == null) {
            comments = post.getComments().stream().sorted(
                    Comparator.comparing(Comment::getDateOfCreated).reversed()
            ).toList();
        }
        else {
            User user = userService.findByEmail(principal.getName());
            comments = new ArrayList<>(post.getComments().stream().sorted(
                    Comparator.comparing(Comment::getDateOfCreated).reversed()
            ).toList());
            comments.removeAll(user.getBlackListOfComments());
            return comments;
        }
        return comments;
    }
    public void setTheSameFieldsForNotification(
            Notification notification,
            Post post,
            TypeOfNotification typeOfNotification
    ) {
        notification.setTypeOfNotification(typeOfNotification);
        notification.setDateOfCreated(new Date());
        notification.setUser(post.getUser());
        notification.setCheckedStatus(false);
        post.getUser().getNotifications().add(notification);
        userService.saveAfterChange(post.getUser());
    }
}
