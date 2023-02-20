package com.example.WriteHere.model;

import com.example.WriteHere.model.image.ImageComment;
import com.example.WriteHere.model.post.Post;
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

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "element", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ImageComment> images = new ArrayList<>();
    @ManyToMany(mappedBy = "likedComments", cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @ToString.Exclude
    private List<User> usersWhoLike = new ArrayList<>();
    @ManyToMany(mappedBy = "dislikedComments", cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @ToString.Exclude
    private List<User> usersWhoDislike = new ArrayList<>();
}
