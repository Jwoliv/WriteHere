package com.example.WriteHere.model.report;

import com.example.WriteHere.model.post.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Component
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ReportByPost extends AbstractReport {
    @ManyToOne
    private Post post;
}
