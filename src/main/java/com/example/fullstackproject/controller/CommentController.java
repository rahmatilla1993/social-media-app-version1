package com.example.fullstackproject.controller;

import com.example.fullstackproject.dto.CommentDto;
import com.example.fullstackproject.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{postId}")
    public HttpEntity<?> getAllCommentsByPost(@PathVariable("postId") int postId) {
        return ResponseEntity.ok(
                commentService.getAllByPost(postId)
        );
    }

    @GetMapping
    public HttpEntity<?> getAllComments() {
        return ResponseEntity.ok(
                commentService.getAll()
        );
    }

    @PostMapping("/{postId}")
    public HttpEntity<?> save(@PathVariable("postId") int postId,
                              @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(
                commentService.save(commentDto, postId)
        );
    }
}
