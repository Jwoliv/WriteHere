package com.example.WriteHere.controller;

import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.notification.TypeOfNotification;
import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.report.ReportByComment;
import com.example.WriteHere.model.user.Role;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.service.CommentsService;
import com.example.WriteHere.service.ConvertMethods;
import com.example.WriteHere.service.report.ReportCommentService;
import com.example.WriteHere.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/comments")
public class CommentsController {
    private final CommentsService commentsService;
    private final UserService userService;
    private final ReportCommentService reportCommentService;
    private final ConvertMethods convertMethods;

    public CommentsController(
            CommentsService commentsService,
            UserService userService,
            ReportCommentService reportCommentService,
            ConvertMethods convertMethods
    ) {
        this.commentsService = commentsService;
        this.userService = userService;
        this.reportCommentService = reportCommentService;
        this.convertMethods = convertMethods;
    }
    @DeleteMapping("/{id}")
    public String pageOfDeleteComment(@PathVariable Long id, Principal principal) {
        Comment comment = commentsService.findById(id);
        Long postId = comment.getPost().getId();
        User userOfSession = userService.findByEmail(principal.getName());
        if (comment.getUser() != null && Objects.equals(comment.getUser().getId(), userOfSession.getId())
                || userOfSession.getRole().equals(Role.ADMIN)) {
                commentsService.deleteById(id);

        }
        if (comment.getUser() == null && userOfSession.getRole().equals(Role.ADMIN)) {
            commentsService.deleteById(id);
        }
        if (userOfSession.getRole().equals(Role.ADMIN)) {
            return "redirect:/admin/posts/" + postId + "/comments";
        }
        return "redirect:/posts/" + postId;
    }
    @PatchMapping("{id}/like")
    public String pageOfLikedComment(
            @PathVariable Long id,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/posts/{id_post}";
        }
        User user = userService.findByEmail(principal.getName());
        Comment comment = commentsService.findById(id);
        comment.setNumberOfLikes(
                convertMethods.changeRating(comment, comment.getNumberOfLikes(), user.getLikedComments())
        );
        user.setLikedComments(
                toggleCommentToTheSecondCollectionOfUser(user.getLikedComments(), comment, user)
        );
        if (user.getDislikedComments().contains(comment)) {
            user.getDislikedComments().remove(comment);
            comment.setNumberOfDislikes(comment.getNumberOfDislikes() - 1);
        }
        if (comment.getUser() != null && !comment.getUser().getId().equals(user.getId())) {
            Notification notification = new Notification();
            notification.setTitle("Your comment is liked");
            notification.setText(
                    "Your comment under post: " + comment.getPost().getTitle() + " was liked by " + user.getFullName()
            );
            setTheSameFieldsForNotification(notification, comment, TypeOfNotification.LikeComment);
        }
        userService.saveAfterChange(user);
        commentsService.save(comment);
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
                toggleCommentToTheSecondCollectionOfUser(user.getDislikedComments(), comment, user)
        );
        if (user.getLikedComments().contains(comment)) {
            user.getLikedComments().remove(comment);
            comment.setNumberOfLikes(comment.getNumberOfLikes() - 1);
        }
        if (comment.getUser() != null && !comment.getUser().getId().equals(user.getId())) {
            Notification notification = new Notification();
            notification.setTitle("Your comment is disliked");
            notification.setText(
                    "Your comment under post: " + comment.getPost().getTitle() + " was disliked by " + user.getFullName()
            );
            setTheSameFieldsForNotification(notification, comment, TypeOfNotification.DislikeComment);
        }
        userService.saveAfterChange(user);
        commentsService.save(comment);
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
        if (!user.getBlackListOfComments().contains(comment)) {
            report.setId(null);
            report.setComment(commentsService.findById(id));
            reportCommentService.save(report);
            user.getBlackListOfComments().add(commentsService.findById(id));
            userService.saveAfterChange(user);
        }
        model.addAttribute("typeOfElement", "comment");
        model.addAttribute("element", comment.getPost());
        return "/success_report";
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
    public void setTheSameFieldsForNotification(
            Notification notification,
            Comment comment,
            TypeOfNotification typeOfNotification
    ) {
        notification.setTypeOfNotification(typeOfNotification);
        notification.setDateOfCreated(new Date());
        notification.setUser(comment.getUser());
        notification.setCheckedStatus(false);
        comment.getUser().getNotifications().add(notification);
        userService.saveAfterChange(comment.getUser());
    }
}
