package com.example.WriteHere.model.notification;

import com.example.WriteHere.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Entity
@Component
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Size(min = 10, max = 500)
    private String text;
    private Date dateOfCreated;
    @Enumerated(EnumType.STRING)
    private TypeOfNotification typeOfNotification;
    @ManyToOne
    private User user;
    private Boolean checkedStatus;
}
