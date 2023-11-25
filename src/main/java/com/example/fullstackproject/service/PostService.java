package com.example.fullstackproject.service;

import com.example.fullstackproject.dto.PostDto;
import com.example.fullstackproject.entity.Post;
import com.example.fullstackproject.entity.User;
import com.example.fullstackproject.exception.ObjectNotFoundException;
import com.example.fullstackproject.payload.response.ApiResponse;
import com.example.fullstackproject.repository.PostRepository;
import com.example.fullstackproject.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final ImageService imageService;
    private final Utils utils;

    @Autowired
    public PostService(PostRepository postRepository,
                       ImageService imageService, Utils utils
    ) {
        this.postRepository = postRepository;
        this.imageService = imageService;
        this.utils = utils;
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
    public Post createPost(MultipartFile multipartFile, PostDto postDto) throws IOException {
        String[] tags = postDto.getTags().split(", ");
        User user = utils.getUser();
        String imageUrl = imageService.uploadImage(multipartFile);
        Post post = new Post();
        post.setText(postDto.getContent());
        post.setTitle(postDto.getTitle());
        post.setTags(Arrays.asList(tags));
        post.setImageUrl(imageUrl);
        post.setViewsCount(0);
        post.setCreatedUser(user);
        post.setCreatedDateTime(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    public Post editPost(MultipartFile multipartFile, PostDto postDto, int postId) throws IOException {
        Post post = getPostById(postId);
        String[] tags = postDto.getTags().split(", ");
        if (multipartFile != null) {
            String imageUrlFromDb = post.getImageUrl();
            imageService.deleteImage(imageUrlFromDb);
            String imageUrl = imageService.uploadImage(multipartFile);
            post.setImageUrl(imageUrl);
        }
        post.setTags(Arrays.asList(tags));
        post.setText(postDto.getContent());
        post.setTitle(postDto.getTitle());
        return post;
    }

    @Transactional
    public Post likePost(int postId) {
        User user = utils.getUser();
        Post post = postRepository
                .findById(postId)
                .orElseThrow(() -> new ObjectNotFoundException("Post not found"));
        if (post.getLikedUsers().contains(user.getEmail())) {
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(user.getEmail());
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(user.getEmail());
        }
        return post;
    }

    @Transactional
    public ApiResponse deletePost(int postId) throws IOException {
        User user = utils.getUser();
        Post userPost = user.getPosts()
                .stream()
                .filter(post -> post.getId() == postId)
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException("Post not found"));
        user.getPosts().remove(userPost);
        imageService.deleteImage(userPost.getImageUrl());
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
