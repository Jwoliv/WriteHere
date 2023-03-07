package com.example.WriteHere.service;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.post.Theme;
import com.example.WriteHere.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
                Comparator.comparing(Post::getDateOfCreated).reversed()
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
        if (post.getReports().size() >= 50) {
            post.setIsSuspicious(true);
        }
        postRepository.save(post);
    }
    @Transactional
    public void deleteById(Long id) {
        Post post = findById(id);
        post.removeUserRelationships();
        save(post);
        postRepository.deleteById(id);
    }

    public List<Post> findByTitleOrText(String name) {
        return postRepository.findByTitleOrText(name.toUpperCase(Locale.ROOT));
    }

    public List<Post> findByTitleOrTextAndUserId(String name, Long id) {
        return postRepository.findByTitleOrTextAndUserId(name, id);
    }
    public List<Post> findByUserId(Long id) {
        return postRepository.findByUserId(id);
    }
}
