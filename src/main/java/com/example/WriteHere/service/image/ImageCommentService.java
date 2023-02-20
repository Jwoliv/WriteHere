package com.example.WriteHere.service.image;

import com.example.WriteHere.model.image.ImageComment;
import com.example.WriteHere.repository.image.ImageCommentRepository;
import org.springframework.stereotype.Service;

@Service
public class ImageCommentService extends ImageService<ImageComment, ImageCommentRepository> {
    protected ImageCommentService(ImageCommentRepository repository) {
        super(repository);
    }
}