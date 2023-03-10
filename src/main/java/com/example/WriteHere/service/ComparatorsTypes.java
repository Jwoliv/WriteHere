package com.example.WriteHere.service;

import com.example.WriteHere.model.post.Comment;
import com.example.WriteHere.model.post.Post;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Getter
@Setter
public class ComparatorsTypes {
    private final PostService postService;
    private final Comparator<Post> comparatorPostsByDateOfCreated = Comparator.comparing(Post::getDateOfCreated).reversed();
    private final Comparator<Comment> comparatorCommentsByDateOfCreated = Comparator.comparing(Comment::getDateOfCreated).reversed();

    public ComparatorsTypes(PostService postService) {
        this.postService = postService;
    }

    public List<Comment> getSortedCommentsByDateOfCreated(List<Comment> comments) {
        return comments
                .stream()
                .sorted(comparatorCommentsByDateOfCreated)
                .toList();
    }
    public List<Post> getSortedPostsByDateOfCreated(List<Post> posts) {
        return posts
                .stream()
                .sorted(comparatorPostsByDateOfCreated)
                .toList();
    }
}
