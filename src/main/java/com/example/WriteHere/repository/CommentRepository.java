package com.example.WriteHere.repository;

import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteAllByPost(Post post);
}
