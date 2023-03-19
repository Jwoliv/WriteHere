package com.example.WriteHere.model.report;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

@MappedSuperclass
@Component
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @NonNull
    protected String message;
    @Enumerated(EnumType.STRING)
    protected ComplaintType complaintType;
}
