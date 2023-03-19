package com.example.WriteHere.service;

import com.example.WriteHere.model.image.ImagePost;
import com.example.WriteHere.model.post.Post;
import com.example.WriteHere.model.post.Theme;
import com.example.WriteHere.model.user.User;
import com.example.WriteHere.repository.PostRepository;
import com.example.WriteHere.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final ConvertMethods convertMethods;
    private final UserService userService;
    @Autowired
    public PostService(PostRepository postRepository, ConvertMethods convertMethods, UserService userService) {
        this.postRepository = postRepository;
        this.convertMethods = convertMethods;
        this.userService = userService;
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
    @Transactional
    public void renewFieldsOfPost(Post postFromDB, Post post) {
        if (postFromDB != null && post != null) {
            postFromDB.setTitle(post.getTitle());
            postFromDB.setText(post.getText());
            postFromDB.setTheme(post.getTheme());
            postFromDB.setIsSuspicious(false);
            save(postFromDB);
        }
    }
    public void setOwnerForPost(Post post, Principal principal) {
        if (principal == null) post.setIsByAnonymous(true);
        else {
            User user = userService.findByEmail(principal.getName());
            user.getPosts().add(post);
            post.setIsByAnonymous(false);
            post.setUser(user);
        }
    }
    @Transactional
    public void setFieldsForPost(Post post, MultipartFile[] images) {
        if (post != null) {
            post.setDateOfCreated(new Date());
            post.setNumberOfLikes(0);
            post.setNumberOfDislikes(0);
            post.setText(convertMethods.convertTextToMarkDown(post.getText()));
            post.setIsSuspicious(false);
            setImagesForPost(post, images);
            save(post);
        }
    }
    @Transactional
    public void setImagesForPost(Post post, MultipartFile[] images) {
        if (Arrays.stream(images).anyMatch(x -> !x.isEmpty())) {
            boolean isPrevious = true;
            for (MultipartFile multipartFile : images) {
                if (!multipartFile.isEmpty()) {
                    convertMethods.setImagesToList(multipartFile, post.getImages(), new ImagePost(), isPrevious);
                }
                isPrevious = false;
            }
            post.getImages().forEach(x -> x.setElement(post));
            save(post);
            post.setPreviousId(post.getImages().get(0).getId());
        }
    }
    public List<Post> togglePostToTheSecondCollectionOfUser(List<Post> collection, Post post, User user) {
        if (collection.contains(post)) {
            collection.remove(post);
            post.getUsersWhoDislike().remove(user);
        }
        else {
            collection.add(post);
            post.getUsersWhoDislike().add(user);
        }
        return collection;
    }
}
