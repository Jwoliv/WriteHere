package com.example.WriteHere.repository;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.post.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findPostsByTheme(Theme theme);
    @Query("SELECT P FROM Post AS P WHERE UPPER(P.title) LIKE %:name% OR UPPER(P.text) LIKE %:name%")
    List<Post> findByTitleOrText(@Param("name") String name);
    @Query("SELECT P FROM Post AS P WHERE P.user.id = :id AND UPPER(P.title) LIKE %:name% OR P.user.id = :id AND UPPER(P.text) LIKE %:name%")
    List<Post> findByTitleOrTextAndUserId(@Param("name") String name, @Param("id") Long id);
}
