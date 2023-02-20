package com.example.WriteHere.model.image;

import com.example.WriteHere.model.Comment;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Getter
@Setter
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
public class ImageComment extends AbstractImage {
    @ManyToOne
    @JoinColumn
    private Comment element;
}