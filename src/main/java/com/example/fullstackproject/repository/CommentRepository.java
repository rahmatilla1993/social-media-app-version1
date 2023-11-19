package com.example.fullstackproject.repository;

import com.example.fullstackproject.entity.Comment;
import com.example.fullstackproject.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByPost(Post post);
}
