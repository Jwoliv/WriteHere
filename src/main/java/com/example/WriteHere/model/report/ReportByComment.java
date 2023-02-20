package com.example.WriteHere.model.report;

import com.example.WriteHere.model.post.Comment;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Entity
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportByComment extends AbstractReport {
    @ManyToOne
    private Comment comment;
}
