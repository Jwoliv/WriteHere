package com.example.WriteHere.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.stereotype.Component;

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
}
