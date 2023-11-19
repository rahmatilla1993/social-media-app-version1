package com.example.fullstackproject.service;

import com.example.fullstackproject.dto.CommentDto;
import com.example.fullstackproject.entity.Comment;
import com.example.fullstackproject.entity.Post;
import com.example.fullstackproject.entity.User;
import com.example.fullstackproject.repository.CommentRepository;
import com.example.fullstackproject.repository.PostRepository;
import com.example.fullstackproject.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final Utils utils;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository, Utils utils) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.utils = utils;
    }

    private Optional<Post> findPostById(int postId) {
        return postRepository.findById(postId);
    }

    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    public List<Comment> getAllByPost(int postId) {
        Optional<Post> optionalPost = findPostById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            return commentRepository.findAllByPost(post);
        }
        return new ArrayList<>();
    }

    @Transactional
    public Comment save(CommentDto commentDto, int postId) {
        User user = utils.getUser();
        Optional<Post> optionalPost = findPostById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Comment comment = new Comment();
            comment.setUser(user);
            comment.setContent(commentDto.getContent());
            comment.setPost(post);
            return commentRepository.save(comment);
        }
        return null;
    }
}
