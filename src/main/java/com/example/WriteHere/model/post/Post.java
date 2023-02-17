package com.example.WriteHere.model.post;

import com.example.WriteHere.model.Comment;
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
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    @Size(min = 1, max = 1000)
    private String text;
    private Date dateOfCreated;
    @Enumerated(EnumType.STRING)
    private Theme theme;
    private Integer numberOfLikes;
    private Integer numberOfDislikes;
    private Boolean isByAnonymous;
    @OneToMany(mappedBy = "post", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();
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
}
