package com.example.WriteHere.controller.image;

import com.example.WriteHere.model.image.AbstractImage;
import com.example.WriteHere.repository.image.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayInputStream;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImagesController<
        T extends AbstractImage,
        R extends ImageRepository<T>
        >{
    private R repository;
    @GetMapping("/images/{id}")
    private ResponseEntity<?> getImageById(@PathVariable Long id) {
        T image = getRepository().findById(id).orElse(null);
        return ResponseEntity.ok()
                .header("fileName", Objects.requireNonNull(image).getOriginalName())
                .contentType(MediaType.valueOf(image.getContentType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }
}