package com.example.WriteHere.service;

import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.post.Theme;
import com.example.WriteHere.repository.PostRepository;
import com.example.WriteHere.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class PostServiceTest {

    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ConvertMethods convertMethods;

    @Mock
    public UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postService = new PostService(postRepository, convertMethods, userService);
    }

    @Test
    void testFindAll_whenDoesNotEmpty() {
        List<Post> posts = new ArrayList<>();
        Post post1 = new Post();
        post1.setId(1L);
        post1.setDateOfCreated(new Date());
        post1.setNumberOfLikes(10);
        post1.setNumberOfDislikes(5);
        post1.setTitle("Post 1");
        post1.setText("This is the first post.");
        post1.setTheme(Theme.it);
        posts.add(post1);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setDateOfCreated(new Date());
        post2.setNumberOfLikes(20);
        post2.setNumberOfDislikes(15);
        post2.setTitle("Post 2");
        post2.setText("This is the second post.");
        post2.setTheme(Theme.games);
        posts.add(post2);

        when(postRepository.findAll()).thenReturn(posts);

        List<Post> result = postService.findAll();

        assertEquals(posts.size(), result.size());
        assertEquals(posts.get(0), result.get(0));
        assertEquals(posts.get(1), result.get(1));
    }
    @Test
    void testFindAll_whenEmpty() {
        when(postRepository.findAll()).thenReturn(new ArrayList<>());
        List<Post> posts = postService.findAll();
        assertEquals(posts.size(), 0);
    }
    @Test
    void testFindById_whenExist() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setDateOfCreated(new Date());
        post1.setNumberOfLikes(10);
        post1.setNumberOfDislikes(5);
        post1.setTitle("Post 1");
        post1.setText("This is the first post.");
        post1.setTheme(Theme.it);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        Post result = postService.findById(1L);
        assertEquals(post1, result);
    }
    @Test
    void testFindById_whenDoesNotExist() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        Post result = postService.findById(1L);
        assertNull(result);
    }
    @Test
    void testFindByTheme_whenExist() {
        List<Post> posts = new ArrayList<>();
        Post post1 = new Post();
        post1.setId(1L);
        post1.setDateOfCreated(new Date());
        post1.setNumberOfLikes(10);
        post1.setNumberOfDislikes(5);
        post1.setTitle("Post 1");
        post1.setText("This is the first post.");
        post1.setTheme(Theme.it);
        posts.add(post1);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setDateOfCreated(new Date());
        post2.setNumberOfLikes(20);
        post2.setNumberOfDislikes(15);
        post2.setTitle("Post 2");
        post2.setText("This is the second post.");
        post2.setTheme(Theme.games);
        posts.add(post2);

        when(postRepository.findPostsByTheme(Theme.it)).thenReturn(posts);
        List<Post> result = postService.findByTheme(Theme.it);
        assertEquals(posts, result);
    }
    @Test
    void testFindByTheme_whenDoesNotExist() {
        when(postRepository.findPostsByTheme(Theme.it)).thenReturn(null);
        assertNull(postService.findByTheme(Theme.it));
    }
}