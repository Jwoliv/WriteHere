package com.example.WriteHere.model.image;

import com.example.WriteHere.model.post.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@ToString
@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class ImagePost extends AbstractImage {
    @ManyToOne
    @JoinColumn
    private Post element;
}