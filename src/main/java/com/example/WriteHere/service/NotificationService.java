package com.example.WriteHere.service;

import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.notification.TypeOfNotification;
import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.user.Role;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.repository.NotificationRepository;
import com.example.WriteHere.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final CommentsService commentsService;
    private final PostService postService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserService userService, CommentsService commentsService, PostService postService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.commentsService = commentsService;
        this.postService = postService;
    }
    public List<Notification> findAllByUserId(Long id) {
        return notificationRepository.findAllByUserId(id);
    }
    public Notification findById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }
    @Transactional
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }
    @Transactional
    public void deleteById(Long id, Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            Notification notification = findById(id);
            if (user.getNotifications().contains(notification) || user.getRole().equals(Role.ADMIN)) {
                notification.setUser(null);
                save(notification);
                notificationRepository.deleteById(id);
            }
        }
    }
    @Transactional
    public void deleteAllByUser(Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            notificationRepository.deleteAllByUser(user);
        }
    }
    @Transactional
    public void setTheSameFieldsForNotificationOfComment(Notification notification, Comment comment, TypeOfNotification typeOfNotification) {
        notification.setTypeOfNotification(typeOfNotification);
        notification.setDateOfCreated(new Date());
        notification.setUser(comment.getUser());
        notification.setCheckedStatus(false);
        comment.getUser().getNotifications().add(notification);
        userService.saveAfterChange(comment.getUser());
    }
    @Transactional
    public void createNotificationForDislikeComment(Comment comment, User user) {
        if (comment.getUser() != null && !comment.getUser().getId().equals(user.getId())) {
            Notification notification = new Notification();
            notification.setTitle("Your comment is disliked");
            notification.setText(
                    "Your comment under post: " + comment.getPost().getTitle() + " was disliked by " + user.getFullName()
            );
            setTheSameFieldsForNotificationOfComment(
                    notification,
                    comment,
                    TypeOfNotification.DislikeComment
            );
        }
        userService.saveAfterChange(user);
        commentsService.save(comment);
    }
    @Transactional
    public void createNotificationsAboutLikePost(Post post, User user) {
        if (
                post != null && user.getLikedPosts().contains(post)
                        && post.getUser() != null && !user.getId().equals(post.getUser().getId())
        ) {
            Notification notification = new Notification();
            notification.setTitle("Your posts is liked");
            notification.setText(
                    "Your posts with name: " + post.getTitle() +
                            " in the theme: " + post.getTheme().getDisplayName() +
                            " was liked by " + user.getFullName()
            );
            setTheSameFieldsForNotificationOfPost(notification, post, TypeOfNotification.LikePost);
        }
    }
    @Transactional
    public void createNotificationsAboutDisLikePost(Post post, User user) {
        if (
                post != null && !user.getDislikedPosts().contains(post)
                        && post.getUser() != null && !user.getId().equals(post.getUser().getId())
        ) {
            Notification notification = new Notification();
            notification.setTitle("Your posts is disliked");
            notification.setText(
                    "Your posts with name: " + post.getTitle() +
                            " in the theme: " + post.getTheme().getDisplayName() +
                            " was disliked by " + user.getFullName()
            );
            setTheSameFieldsForNotificationOfPost(notification, post, TypeOfNotification.DislikePost);
        }
    }
    @Transactional
    public void setTheSameFieldsForNotificationOfPost(Notification notification, Post post, TypeOfNotification typeOfNotification) {
        if (notification != null && post != null) {
            notification.setTypeOfNotification(typeOfNotification);
            notification.setDateOfCreated(new Date());
            notification.setUser(post.getUser());
            notification.setCheckedStatus(false);
            post.getUser().getNotifications().add(notification);
            userService.saveAfterChange(post.getUser());
        }
    }
    @Transactional
    public void createNotificationAboutAddComment(Post post, Principal principal, Comment comment) {
        if (comment != null) {
            if (post != null && post.getUser() != null) {
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
                setTheSameFieldsForNotificationOfPost(notification, post, TypeOfNotification.NewComment);
            }
            commentsService.save(comment);
        }
    }
    @Transactional
    public void createNotificationAboutDeleteComment(Post post, Principal principal, User user, Long id) {
        if (user != null && user.getRole().equals(Role.ADMIN) || post.getUser() != null && post.getUser().equals(user)) {
            User userOfTheSession = userService.findByEmail(principal.getName());
            if (user.getRole().equals(Role.ADMIN) && post.getUser() != null && userOfTheSession != post.getUser()) {
                Notification notification = new Notification();
                notification.setTitle("Your posts is deleted");
                notification.setText(
                        "Your posts with name: " + post.getTitle() +
                                " in the theme: " + post.getTheme().getDisplayName() + " was deleted by admin"
                );
                setTheSameFieldsForNotificationOfPost(notification, post, TypeOfNotification.PostIsDeleted);
            }
            postService.deleteById(id);
            userService.saveAfterChange(user);
        }
    }
    @Transactional
    public void checkNotificationById(Long id, Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            Notification notification = findById(id);
            if (user.getNotifications().contains(notification)) {
                notification.setCheckedStatus(true);
                save(notification);
            }
        }
    }
}
