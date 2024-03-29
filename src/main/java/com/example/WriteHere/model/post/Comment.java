package com.example.WriteHere.model.post;

import com.example.WriteHere.model.image.ImageComment;
import com.example.WriteHere.model.report.ReportByComment;
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
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 1, max = 1000)
    private String text;
    private Date dateOfCreated;
    private Integer numberOfLikes;
    private Integer numberOfDislikes;
    private Boolean isByAnonymous;
    private Boolean isSuspicious;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    private Post post;
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    private User user;
    @OneToMany(mappedBy = "element", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ImageComment> images = new ArrayList<>();
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ReportByComment> reports = new ArrayList<>();
    @ManyToMany(mappedBy = "likedComments", cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private List<User> usersWhoLike = new ArrayList<>();
    @ManyToMany(mappedBy = "dislikedComments", cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private List<User> usersWhoDislike = new ArrayList<>();
    @ManyToMany(mappedBy = "blackListOfComments", cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @ToString.Exclude
    private List<User> usersWhoBlock = new ArrayList<>();

    public void removeFields() {
        this.getUsersWhoDislike().forEach(user -> user.getDislikedComments().remove(this));
        this.getUsersWhoLike().forEach(user -> user.getLikedComments().remove(this));
        this.getUsersWhoBlock().forEach(user -> user.getBlackListOfComments().remove(this));
    }
}
