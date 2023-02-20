package com.example.WriteHere.controller;

import com.example.WriteHere.model.Comment;
import com.example.WriteHere.model.image.AbstractImage;
import com.example.WriteHere.model.image.ImageComment;
import com.example.WriteHere.model.image.ImagePost;
import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.CommentsService;
import com.example.WriteHere.service.PostService;
import com.example.WriteHere.service.user.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostsController {
    private final PostService postService;
    private final UserService userService;
    private final CommentsService commentsService;
    @Autowired
    public PostsController(PostService postService, UserService userService, CommentsService commentsService) {
        this.postService = postService;
        this.userService = userService;
        this.commentsService = commentsService;
    }

    @GetMapping()
    public String pageOfAllPosts(
            @NonNull Model model,
            Principal principal
    ) {
        model.addAttribute("all_posts", postService.findAll().stream().sorted(
                (x1, x2) -> x2.getDateOfCreated().compareTo(x1.getDateOfCreated())
        ));
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
        model.addAttribute("all_posts", postService.findByTitleOrText(name));
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
        model.addAttribute("comments", post.getComments().stream().sorted(
                (x1, x2) -> x2.getDateOfCreated().compareTo(x1.getDateOfCreated())
        ));
        model.addAttribute("principal", principal);
        model.addAttribute("comment", new Comment());
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
    @GetMapping("/{id}/edit")
    public String pageOfEditedPost(
            @PathVariable Long id,
            @NonNull Model model,
            Principal principal
    ) {
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("principal", principal);
        return "/posts/edit_post";
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

        if (Arrays.stream(images).anyMatch(x -> !x.isEmpty())) {
            boolean flag = true;
            for (MultipartFile multipartFile : images) {
                if (!multipartFile.isEmpty()) {
                    setImagesToList(multipartFile, post.getImages(), new ImagePost(), flag);
                }
                flag = false;
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
        postService.save(postFromDB);
        return "redirect:/posts/{id}";
    }
    @PatchMapping("/{id}/like")
    public String likedPost(@PathVariable Long id, Principal principal) {
        Post post = postService.findById(id);
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            post.setNumberOfLikes(
                    changeRating(post, post.getNumberOfLikes(), user.getLikedPosts())
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
        return "redirect:/posts/{id}";
    }
    @PatchMapping("/{id}/dislike")
    public String dislikedPost(@PathVariable Long id, Principal principal) {
        Post post = postService.findById(id);
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            post.setNumberOfDislikes(
                    changeRating(post, post.getNumberOfDislikes(), user.getDislikedPosts())
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
        return "redirect:/posts/{id}";
    }
    @PostMapping("/{id}/add_comment")
    public String saveNewComment(
            @PathVariable Long id,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            Principal principal,
            @ModelAttribute Comment comment,
            BindingResult bindingResult
    ) {
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

        if (Arrays.stream(images).anyMatch(x -> !x.isEmpty())) {
            boolean flag = true;
            for (MultipartFile multipartFile : images) {
                if (!multipartFile.isEmpty()) {
                    setImagesToList(multipartFile, comment.getImages(), new ImageComment(), flag);
                }
                flag = false;
            }
            comment.getImages().forEach(x -> x.setElement(comment));
        }

        commentsService.save(comment);
        postService.save(post);
        return "redirect:/posts/{id}";
    }
    @PatchMapping("/{id_post}/comments/{id_comment}/like")
    public String pageOfLikedComment(
            @PathVariable Long id_post,
            @PathVariable Long id_comment,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/posts/{id_post}";
        }
        User user = userService.findByEmail(principal.getName());
        Comment comment = commentsService.findById(id_comment);
        comment.setNumberOfLikes(
                changeRating(comment, comment.getNumberOfLikes(), user.getLikedComments())
        );
        user.setLikedComments(
                toggleCommentToTheSecondCollectionOfUser(user.getLikedComments(), comment, user)
        );
        if (user.getDislikedComments().contains(comment)) {
            user.getDislikedComments().remove(comment);
            comment.setNumberOfDislikes(comment.getNumberOfDislikes() - 1);
        }
        userService.saveAfterChange(user);
        commentsService.save(comment);
        return "redirect:/posts/{id_post}";
    }
    @PatchMapping("/{id_post}/comments/{id_comment}/dislike")
    public String pageOfDislikedComment(
            @PathVariable("id_post") Long id_post,
            @PathVariable("id_comment") Long id_comment,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/posts/{id_post}";
        }
        User user = userService.findByEmail(principal.getName());
        Comment comment = commentsService.findById(id_comment);
        comment.setNumberOfDislikes(
                changeRating(comment, comment.getNumberOfDislikes(), user.getDislikedComments())
        );
        user.setDislikedComments(
                toggleCommentToTheSecondCollectionOfUser(user.getDislikedComments(), comment, user)
        );
        if (user.getLikedComments().contains(comment)) {
            user.getLikedComments().remove(comment);
            comment.setNumberOfLikes(comment.getNumberOfLikes() - 1);
        }
        userService.saveAfterChange(user);
        commentsService.save(comment);
        return "redirect:/posts/{id_post}";
    }

    public <T> Integer changeRating(T element, Integer value, List<T> elementsByUser) {
        if (elementsByUser.contains(element)) return --value;
        return ++value;
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
    public List<Comment> toggleCommentToTheSecondCollectionOfUser(List<Comment> collection, Comment comment, User user) {
        if (collection.contains(comment)) {
            collection.remove(comment);
            comment.getUsersWhoDislike().remove(user);
        }
        else {
            collection.add(comment);
            comment.getUsersWhoDislike().add(user);
        }
        return collection;
    }
    public <T extends AbstractImage> void setImagesToList(
            MultipartFile multipartFile,
            List<T> images,
            T imageEmpty,
            Boolean isPrevious
    ) {
        T image = convertToImage(multipartFile, isPrevious, imageEmpty);
        images.add(image);
    }

    public <T extends AbstractImage> T convertToImage(
            MultipartFile file,
            Boolean isPreviews,
            T image
    ) {
        image.setName(file.getOriginalFilename());
        image.setOriginalName(file.getOriginalFilename());
        image.setSize(file.getSize());
        image.setContentType(file.getContentType());
        image.setIsPreviews(isPreviews);
        try {
            image.setBytes(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }
}
