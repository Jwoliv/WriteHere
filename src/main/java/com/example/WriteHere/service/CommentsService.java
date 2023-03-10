package com.example.WriteHere.service;

import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentsService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentsService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }
    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }
    @Transactional
    public void save(Comment comment) {
        if (comment.getReports().size() >= 5) {
            comment.setIsSuspicious(true);
        }
        commentRepository.save(comment);
    }
    @Transactional
    public void deleteById(Long id) {
        Comment comment = findById(id);
        comment.removeFields();
        commentRepository.deleteById(id);
    }
    @Transactional
    public void deleteAllByPost(Post post) {
        commentRepository.deleteAllByPost(post);
    }
}
