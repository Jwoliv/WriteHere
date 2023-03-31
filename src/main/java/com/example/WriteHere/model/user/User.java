package com.example.WriteHere.model.user;

import com.example.WriteHere.model.notification.Notification;
import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.post.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Entity
@Component
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @Transient
    private String fullName;
    @Enumerated(EnumType.STRING)
    private Role role;
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;
    private boolean active;
    @NotBlank
    private String password;
    @Transient
    private String repeatPassword;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Post> posts = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinTable(
            name = "user_liked_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "liked_post_id")
    )
    @ToString.Exclude
    private List<Post> likedPosts = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinTable(
            name = "user_disliked_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "disliked_post_id")
    )
    @ToString.Exclude
    private List<Post> dislikedPosts = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinTable(
            name = "user_liked_comments",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "liked_comment_id")
    )
    @ToString.Exclude
    private List<Comment> likedComments = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinTable(
            name = "user_disliked_comments",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "disliked_comment_id")
    )
    @ToString.Exclude
    private List<Comment> dislikedComments = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinTable(
            name = "block_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    @ToString.Exclude
    private List<Post> blackListOfPosts = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinTable(
            name = "block_comments",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    @ToString.Exclude
    private List<Comment> blackListOfComments = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Notification> notifications = new ArrayList<>();
    private Boolean isPrivate;


    public String getFullName() {
        return firstname + " " + lastname;
    }
    public Float getReputation() {
        int likes = countLikes(this.posts, this.comments);
        int dislike = countDislikes(this.posts, this.comments);
        if (dislike != 0) {
            return (float) (likes / dislike);
        }
        else {
            return (float) likes;
        }
    }
    public int getNumberOfReports() {
        int count = 0;
        count += this.getPosts().stream().mapToInt(post -> post.getReports().size()).sum();
        count += this.getComments().stream().mapToInt(comment -> comment.getReports().size()).sum();
        return count;
    }


    private int countLikes(List<Post> posts, List<Comment> comments) {
        int likes = 0;
        likes += posts.parallelStream().mapToInt(Post::getNumberOfLikes).sum();
        likes += comments.parallelStream().mapToInt(Comment::getNumberOfLikes).sum();
        return likes;
    }

    private int countDislikes(List<Post> posts, List<Comment> comments) {
        int dislikes = 0;
        dislikes += posts.parallelStream().mapToInt(Post::getNumberOfDislikes).sum();
        dislikes += comments.parallelStream().mapToInt(Comment::getNumberOfDislikes).sum();
        return dislikes;
    }
}
