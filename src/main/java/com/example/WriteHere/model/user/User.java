package com.example.WriteHere.model.user;

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
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    private List<Post> posts = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "user_liked_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "liked_post_id")
    )
    @ToString.Exclude
    private List<Post> likedPosts = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "user_disliked_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "disliked_post_id")
    )
    @ToString.Exclude
    private List<Post> dislikedPosts = new ArrayList<>();
    public String getFullName() {
        return firstname + " " + lastname;
    }
}
