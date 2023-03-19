package com.example.WriteHere.service;

import com.example.WriteHere.model.image.ImageComment;
import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.notification.TypeOfNotification;
import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.report.ReportByComment;
import com.example.WriteHere.model.user.Role;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.repository.CommentRepository;
import com.example.WriteHere.service.report.ReportCommentService;
import com.example.WriteHere.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class CommentsService {
    private final CommentRepository commentRepository;
    private NotificationService notificationService;
    private final UserService userService;
    private final ReportCommentService reportCommentService;
    private final ComparatorsTypes comparatorsTypes;
    private final ConvertMethods convertMethods;

    @Autowired
    public CommentsService(
            CommentRepository commentRepository,
            UserService userService,
            ReportCommentService reportCommentService,
            ComparatorsTypes comparatorsTypes,
            ConvertMethods convertMethods
    ) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.reportCommentService = reportCommentService;
        this.comparatorsTypes = comparatorsTypes;
        this.convertMethods = convertMethods;
    }
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }
    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }
    @Transactional
    public void save(Comment comment) {
        if (comment.getReports().size() >= 5) {
            comment.setIsSuspicious(true);
        }
        commentRepository.save(comment);
    }
    @Transactional
    public void deleteById(Long id) {
        Comment comment = findById(id);
        comment.removeFields();
        commentRepository.deleteById(id);
    }
    @Transactional
    public void deleteAllByPost(Post post) {
        commentRepository.deleteAllByPost(post);
    }
    @Transactional
    public void createReportForComment(User user, Comment comment, ReportByComment report, Long id) {
        if (!user.getBlackListOfComments().contains(comment)) {
            report.setId(null);
            report.setComment(findById(id));
            reportCommentService.save(report);
            user.getBlackListOfComments().add(findById(id));
            userService.saveAfterChange(user);
        }
    }
    public void dislikeComment(Comment comment, User user) {
        if (user.getDislikedComments().contains(comment)) {
            user.getDislikedComments().remove(comment);
            comment.setNumberOfDislikes(comment.getNumberOfDislikes() - 1);
        }
    }
    public void likeComment(Comment comment, User user) {
        if (user.getLikedComments().contains(comment)) {
            user.getLikedComments().remove(comment);
            comment.setNumberOfLikes(comment.getNumberOfLikes() - 1);
        }
    }
    @Transactional
    public void validationOfDeleteComment(Comment comment, Long id, User userOfSession) {
        if (comment.getUser() != null && Objects.equals(comment.getUser().getId(), userOfSession.getId())
                || userOfSession.getRole().equals(Role.ADMIN)
        ) {
            deleteById(id);
        }
        if (comment.getUser() == null && userOfSession.getRole().equals(Role.ADMIN)) {
            deleteById(id);
        }
    }
    @Transactional
    public void createNotificationForLikedComment(Comment comment, User user) {
        if (comment.getUser() != null && !comment.getUser().getId().equals(user.getId())) {
            Notification notification = new Notification();
            notification.setTitle("Your comment is liked");
            notification.setText(
                    "Your comment under post: " + comment.getPost().getTitle() + " was liked by " + user.getFullName()
            );
            notificationService.setTheSameFieldsForNotificationOfComment(
                    notification,
                    comment,
                    TypeOfNotification.LikeComment
            );
        }
        userService.saveAfterChange(user);
        save(comment);
    }
    public List<Comment> toggleCommentToTheSecondCollectionOfUser(
            List<Comment> collection,
            Comment comment,
            User user
    ) {
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
    public List<Comment> findComments(Principal principal, Post post) {
        List<Comment> comments;
        if (principal == null) {
            comments = comparatorsTypes.getSortedCommentsByDateOfCreated(post.getComments());
        }
        else {
            User user = userService.findByEmail(principal.getName());
            comments = new ArrayList<>(comparatorsTypes.getSortedCommentsByDateOfCreated(post.getComments()));
            comments.removeAll(user.getBlackListOfComments());
            return comments;
        }
        return comments;
    }
    @Transactional
    public void setFieldsForComment(Comment comment, Post post, MultipartFile[] images) {
        if (post != null && comment != null) {
            comment.setPost(post);
            post.getComments().add(comment);
            comment.setNumberOfDislikes(0);
            comment.setNumberOfLikes(0);
            comment.setDateOfCreated(new Date());
            comment.setText(convertMethods.convertTextToMarkDown(comment.getText()));
            comment.setIsSuspicious(false);
            setImagesForComment(images, comment);
        }
    }
    @Transactional
    public void setImagesForComment(MultipartFile[] images, Comment comment) {
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
    }
    @Transactional
    public void setOwnerOfComment(Comment comment, Principal principal) {
        if (comment != null) {
            comment.setId(null);
            if (principal == null) {
                comment.setIsByAnonymous(true);
            } else {
                User user = userService.findByEmail(principal.getName());
                user.getComments().add(comment);
                comment.setIsByAnonymous(false);
                comment.setUser(user);
            }
        }
    }
}
