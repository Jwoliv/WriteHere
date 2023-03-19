package com.example.WriteHere.controller;

import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.report.ReportByComment;
import com.example.WriteHere.model.user.Role;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.CommentsService;
import com.example.WriteHere.service.ConvertMethods;
import com.example.WriteHere.service.NotificationService;
import com.example.WriteHere.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/comments")
public class CommentsController {
    private final CommentsService commentsService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ConvertMethods convertMethods;

    public CommentsController(
            CommentsService commentsService,
            UserService userService,
            NotificationService notificationService, ConvertMethods convertMethods
    ) {
        this.commentsService = commentsService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.convertMethods = convertMethods;
    }
    @DeleteMapping("/{id}")
    public String pageOfDeleteComment(@PathVariable Long id, Principal principal) {
        Comment comment = commentsService.findById(id);
        Long postId = comment.getPost().getId();
        User userOfSession = userService.findByEmail(principal.getName());
        commentsService.validationOfDeleteComment(comment, id, userOfSession);
        if (userOfSession.getRole().equals(Role.ADMIN)) {
            return "redirect:/admin/posts/" + postId + "/comments";
        }
        return "redirect:/posts/" + postId;
    }
    @PatchMapping("{id}/like")
    public String pageOfLikedComment(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/posts/{id_post}";
        }
        User user = userService.findByEmail(principal.getName());
        Comment comment = commentsService.findById(id);
        comment.setNumberOfLikes(
                convertMethods.changeRating(comment, comment.getNumberOfLikes(), user.getLikedComments())
        );
        user.setLikedComments(
                commentsService.toggleCommentToTheSecondCollectionOfUser(user.getLikedComments(), comment, user)
        );
        commentsService.dislikeComment(comment, user);
        commentsService.createNotificationForLikedComment(comment, user);
        return "redirect:/posts/" + comment.getPost().getId();
}
    @PatchMapping("{id}/dislike")
    public String pageOfDislikedComment(
            @PathVariable Long id,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/posts/" + commentsService.findById(id).getPost().getId() ;
        }
        User user = userService.findByEmail(principal.getName());
        Comment comment = commentsService.findById(id);
        comment.setNumberOfDislikes(
                convertMethods.changeRating(comment, comment.getNumberOfDislikes(), user.getDislikedComments())
        );
        user.setDislikedComments(
                commentsService.toggleCommentToTheSecondCollectionOfUser(user.getDislikedComments(), comment, user)
        );
        commentsService.likeComment(comment, user);
        notificationService.createNotificationForDislikeComment(comment, user);
        return "redirect:/posts/" + comment.getPost().getId();
    }
    @PostMapping("{id}/report")
    public String createNewReportForComment(
            Principal principal,
            Model model,
            @PathVariable Long id,
            @ModelAttribute("report") @Valid ReportByComment report,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "redirect:/posts/" + commentsService.findById(id).getPost().getId();
        }
        if (principal == null) {
            return "redirect:/posts/{id}";
        }
        User user = userService.findByEmail(principal.getName());
        Comment comment = commentsService.findById(id);
        commentsService.createReportForComment(user, comment, report, id);
        model.addAttribute("typeOfElement", "comment");
        model.addAttribute("element", comment.getPost());
        return "/success_report";
    }
}
