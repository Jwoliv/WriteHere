package com.example.WriteHere.service.image;

import com.example.WriteHere.model.image.ImagePost;
import com.example.WriteHere.repository.image.ImagePostRepository;
import org.springframework.stereotype.Service;

@Service
public class ImagePostService extends ImageService<ImagePost, ImagePostRepository> {
    protected ImagePostService(ImagePostRepository repository) {
        super(repository);
    }
}