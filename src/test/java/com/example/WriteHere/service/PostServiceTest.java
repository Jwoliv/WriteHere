package com.example.WriteHere.service;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.post.Theme;
import com.example.WriteHere.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class PostServiceTest {
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        postService = new PostService(postRepository);
    }

    //region Test looking for all posts
    @Test
    public void when_findAllPosts_areAllExist() {
        List<Post> posts = new ArrayList<>();
        Post post1 = new Post();
        post1.setTitle("First Post");
        post1.setText("This is the first post.");
        posts.add(post1);

        Post post2 = new Post();
        post2.setTitle("Second Post");
        post2.setText("This is the second post.");
        posts.add(post2);

        List<Post> result = List.of(post1, post2);

        when(postRepository.findAll()).thenReturn(posts);

        assertEquals(posts.size(), result.size());
        assertEquals(posts, result);
    }
    @Test
    public void when_findAllPosts_isOneExist() {
        List<Post> posts = new ArrayList<>();
        Post post1 = new Post();
        post1.setTitle("First Post");
        post1.setText("This is the first post.");
        posts.add(post1);

        List<Post> result = List.of(post1);

        when(postRepository.findAll()).thenReturn(posts);

        assertEquals(posts.size(), result.size());
        assertEquals(posts, result);
    }

    @Test
    public void when_findAllPosts_isEmptyList() {
        when(postRepository.findAll()).thenReturn(null);
        List<Post> result = postRepository.findAll();
        assertNull(result);
    }
    //endregion

    //region Tests for looking for posts by theme
    @Test
    public void when_findByTheme_isOnePost() {
        Theme theme = Theme.it;

        Post post1 = new Post();
        post1.setTitle("First Post");
        post1.setText("This is the first post.");
        post1.setTheme(theme);

        List<Post> listByTheme = List.of(post1);

        when(postRepository.findPostsByTheme(theme)).thenReturn(listByTheme);

        List<Post> result = postService.findByTheme(theme);
        assertEquals(listByTheme.size(), result.size());
        assertEquals(listByTheme, result);
        verify(postRepository, times(1)).findPostsByTheme(theme);
    }
    @Test
    public void when_findByTheme_isAllPosts() {
        Theme theme = Theme.it;

        Post post1 = new Post();
        post1.setTitle("First Post");
        post1.setText("This is the first post.");
        post1.setTheme(theme);

        Post post2 = new Post();
        post2.setTitle("Second Post");
        post2.setText("This is the second post.");
        post2.setTheme(theme);

        List<Post> posts = List.of(post1, post2);

        when(postRepository.findPostsByTheme(theme)).thenReturn(posts);

        List<Post> result = postService.findByTheme(theme);
        assertEquals(posts.size(), result.size());
        assertEquals(posts, result);
        verify(postRepository, times(1)).findPostsByTheme(theme);
    }
    @Test
    public void when_findByTheme_isNotAnyPosts() {
        Theme theme = Theme.it;
        Theme theme1 = Theme.games;

        Post post1 = new Post();
        post1.setTitle("First Post");
        post1.setText("This is the first post.");
        post1.setTheme(theme);

        Post post2 = new Post();
        post2.setTitle("Second Post");
        post2.setText("This is the second post.");
        post2.setTheme(theme);

        when(postRepository.findPostsByTheme(theme1)).thenReturn(null);

        List<Post> result = postService.findByTheme(theme1);

        assertNull(result);
        verify(postRepository, times(1)).findPostsByTheme(theme1);
    }
    //endregion

    //region Tests for looking post by id
    @Test
    public void when_findById_expect_isExistedPost() {
        Long id = 1L;
        Post post = new Post();
        post.setId(id);
        post.setTitle("Test Post");
        post.setText("This is a test post.");

        when(postRepository.findById(id)).thenReturn(java.util.Optional.of(post));

        Post result = postService.findById(id);
        assertEquals(post, result);
        verify(postRepository, times(1)).findById(id);
    }
    @Test
    public void when_findById_expect_isNotExistedPost() {
        Post result = postService.findById(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertNull(result);
        verify(postRepository, times(1)).findById(1L);
    }
    //endregion Tests for looking post by id

    //region Test for CRUD operations for post
    @Test
    public void when_testSave_isSuccessfully() {
        Post post = new Post();
        post.setTitle("Test Post");
        post.setText("This is a test post.");

        postService.save(post);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void when_deleteById_isSuccessfully() {
        Long id = 1L;
        Post post = new Post();
        post.setId(id);
        post.setTitle("Test Post");
        post.setText("This is a test post.");

        when(postRepository.findById(id)).thenReturn(java.util.Optional.of(post));

        postService.deleteById(id);
        verify(postRepository, times(1)).findById(id);
        verify(postRepository, times(1)).deleteById(id);
    }
    //endregion
}