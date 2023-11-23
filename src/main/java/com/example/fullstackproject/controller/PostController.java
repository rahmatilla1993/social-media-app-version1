package com.example.fullstackproject.controller;

import com.example.fullstackproject.dto.PostDto;
import com.example.fullstackproject.entity.Post;
import com.example.fullstackproject.exception.ObjectNotFoundException;
import com.example.fullstackproject.payload.response.ApiResponse;
import com.example.fullstackproject.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public HttpEntity<List<Post>> getAll(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "order", required = false) String order
    ) {
        return ResponseEntity.ok(
                postService.getAll(title, tag, order)
        );
    }

    @GetMapping("/{postId}")
    public HttpEntity<?> getPostById(@PathVariable("postId") int postId) {
        return ResponseEntity.ok(
                postService.getPostById(postId)
        );
    }

    @PostMapping(value = "/add")
    public HttpEntity<?> createPost(
            @RequestPart("file") MultipartFile multipartFile,
            @RequestPart("data") PostDto postDto
    ) throws IOException {
        return ResponseEntity.ok(
                postService.createPost(multipartFile, postDto)
        );
    }

    @PutMapping("/{postId}/edit")
    public HttpEntity<?> editPost(
            @RequestPart(value = "file", required = false) MultipartFile multipartFile,
            @RequestPart("data") PostDto postDto,
            @PathVariable("postId") int postId
    ) throws IOException {
        return ResponseEntity.ok(
                postService.editPost(multipartFile, postDto, postId)
        );
    }

    @DeleteMapping("/{postId}/delete")
    public HttpEntity<?> deletePost(@PathVariable("postId") int postId) throws IOException {
        return ResponseEntity.ok(
                postService.deletePost(postId)
        );
    }

    @GetMapping("/allTags")
    public HttpEntity<?> getAllTags() {
        return ResponseEntity.ok(
                postService.getTagsAllPosts()
        );
    }

    @ExceptionHandler
    public HttpEntity<?> handleException(ObjectNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(e.getMessage(), false));
    }
}
