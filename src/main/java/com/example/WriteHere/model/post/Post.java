package com.example.WriteHere.model.post;

import com.example.WriteHere.model.image.ImagePost;
import com.example.WriteHere.model.report.ReportByPost;
import com.example.WriteHere.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Component
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    @Size(min = 1, max = 5000)
    private String text;
    private Date dateOfCreated;
    @Enumerated(EnumType.STRING)
    private Theme theme;
    private Integer numberOfLikes;
    private Integer numberOfDislikes;
    private Boolean isByAnonymous;
    private Long previousId;
    private Boolean isSuspicious;
    @OneToMany(mappedBy = "element", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ImagePost> images = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ReportByPost> reports = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany(
            mappedBy = "likedPosts",
            cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST}
    )
    @ToString.Exclude
    private List<User> usersWhoLike = new ArrayList<>();
    @ManyToMany(
            mappedBy = "dislikedPosts",
            cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST}
    )
    @ToString.Exclude
    private List<User> usersWhoDislike = new ArrayList<>();
    @ManyToMany(
            mappedBy = "blackListOfPosts",
            cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST}
    )
    @ToString.Exclude
    private List<User> usersWhoBlock = new ArrayList<>();
    public void removeUserRelationships() {
        this.getUsersWhoLike().forEach(user -> user.getLikedPosts().remove(this));
        this.getUsersWhoDislike().forEach(user -> user.getDislikedPosts().remove(this));
        this.getUsersWhoBlock().forEach(user -> user.getBlackListOfPosts().remove(this));
        this.getComments().forEach(comment -> {
            comment.getUsersWhoLike().forEach(user -> user.getLikedComments().remove(comment));
            comment.getUsersWhoDislike().forEach(user -> user.getDislikedComments().remove(comment));
            comment.getUsersWhoBlock().forEach(user -> user.getBlackListOfComments().remove(comment));
        });
    }
}
