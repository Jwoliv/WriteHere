package com.example.WriteHere.model.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Date;

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
}
