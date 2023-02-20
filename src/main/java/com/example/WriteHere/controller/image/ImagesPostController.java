package com.example.WriteHere.controller.image;


import com.example.WriteHere.model.image.ImagePost;
import com.example.WriteHere.repository.image.ImagePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class ImagesPostController extends ImagesController<ImagePost, ImagePostRepository> {
    private final ImagePostRepository categoryRepository;

    @Autowired
    public ImagesPostController(ImagePostRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ImagePostRepository getRepository() {
        return categoryRepository;
    }
}