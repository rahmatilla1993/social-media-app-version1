package com.example.fullstackproject.service;

import com.example.fullstackproject.dto.PostDto;
import com.example.fullstackproject.entity.ImageModel;
import com.example.fullstackproject.entity.Post;
import com.example.fullstackproject.entity.User;
import com.example.fullstackproject.exception.ObjectNotFoundException;
import com.example.fullstackproject.payload.response.ApiResponse;
import com.example.fullstackproject.repository.ImageRepository;
import com.example.fullstackproject.repository.PostRepository;
import com.example.fullstackproject.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PostService {

    @Value("${uploaded-path}")
    private String uploadPath;

    private final PostRepository postRepository;
    private final Utils utils;
    private final ImageRepository imageRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                       ImageRepository imageRepository,
                       Utils utils
    ) {
        this.postRepository = postRepository;
        this.utils = utils;
        this.imageRepository = imageRepository;
    }

    public List<Post> getAll(String title, String tag, String order) {
        List<Post> result;
        if (title != null && tag != null) {
            result = postRepository.findAllByTitleIsContainingIgnoreCaseAndTagsContainingIgnoreCase(title, tag);
        } else if (title != null) {
            result = postRepository.findAllByTitleIsContainingIgnoreCase(title);
        } else if (tag != null) {
            result = postRepository.findAllByTagsContainingIgnoreCase(tag);
        } else if (order != null) {
            result = postRepository.getAllByOrderByViewsCountDesc();
        } else result = postRepository.findAllByOrderByCreatedDateTimeDesc();
        return result;
    }

    @Transactional
    public Post getPostById(int postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setViewsCount(post.getViewsCount() + 1);
            return post;
        }
        return null;
    }

    @Transactional
    public Post createPost(PostDto postDto) {
        String[] tags = postDto.getTags().split(", ");
        User user = utils.getUser();
        Post post = new Post();
        post.setText(postDto.getContent());
        post.setTitle(postDto.getTitle());
        post.setTags(Arrays.asList(tags));
        post.setViewsCount(0);
        post.setCreatedUser(user);
        post.setCreatedDateTime(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    public Post editPost(PostDto postDto, int postId) {
        Post post = getPostById(postId);
        String[] tags = postDto.getTags().split(", ");
        post.setTags(Arrays.asList(tags));
        post.setText(postDto.getContent());
        post.setTitle(postDto.getTitle());
        return post;
    }

    @Transactional
    public ApiResponse deletePost(int postId) {
        User user = utils.getUser();
        Path path = Path.of(uploadPath);
        Post userPost = user.getPosts()
                .stream()
                .filter(post -> post.getId() == postId)
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException("Post not found"));
        Optional<ImageModel> optionalImageModel = imageRepository
                .findByImagePost(userPost);
        if (optionalImageModel.isPresent()) {
            ImageModel imageModel = optionalImageModel.get();
            imageRepository.delete(imageModel);
            try {
                Files.delete(path.resolve(imageModel.getGeneratedName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        user.getPosts().remove(userPost);
        postRepository.delete(userPost);
        return new ApiResponse("Post deleted", true);
    }

    public List<String> getTagsAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(Post::getTags)
                .toList()
                .stream()
                .flatMap(Collection::stream)
                .distinct()
                .limit(7)
                .toList();
    }
}
