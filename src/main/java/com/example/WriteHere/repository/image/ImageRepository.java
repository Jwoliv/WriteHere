package com.example.WriteHere.repository.image;

import com.example.WriteHere.model.image.AbstractImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository<T extends AbstractImage> extends JpaRepository<T, Long> { }