package com.example.WriteHere.service;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.post.Theme;
import com.example.WriteHere.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAll().stream().sorted(
                (x1, x2) -> x2.getDateOfCreated().compareTo(x1.getDateOfCreated())
        ).toList();
    }
    public List<Post> findByTheme(Theme theme) {
        return postRepository.findPostsByTheme(theme);
    }
    public Post findById(Long id) {
        return postRepository.findById(id).orElse(null);
    }
    @Transactional
    public void save(Post post) {
        postRepository.save(post);
    }
    @Transactional
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}