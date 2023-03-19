package com.example.WriteHere.model.image;

import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected String name;
    protected String originalName;
    protected Long size;
    protected String contentType;
    protected Boolean isPreviews;
    @Lob
    protected byte[] bytes;
}