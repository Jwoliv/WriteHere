package com.example.WriteHere.repository;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.post.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findPostsByTheme(Theme theme);
}
