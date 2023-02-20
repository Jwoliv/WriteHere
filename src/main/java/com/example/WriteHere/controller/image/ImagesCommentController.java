package com.example.WriteHere.controller.image;

import com.example.WriteHere.model.image.ImageComment;
import com.example.WriteHere.repository.image.ImageCommentRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
public class ImagesCommentController extends ImagesController<ImageComment, ImageCommentRepository> {
    private final ImageCommentRepository imageCommentRepository;
    public ImagesCommentController(ImageCommentRepository imageCommentRepository) {
        this.imageCommentRepository = imageCommentRepository;
    }
    @Override
    public ImageCommentRepository getRepository() {
        return imageCommentRepository;
    }
}