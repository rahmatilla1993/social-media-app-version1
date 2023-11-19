package com.example.fullstackproject.repository;

import com.example.fullstackproject.entity.Post;
import com.example.fullstackproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findAllByOrderByCreatedDateTimeDesc();

    List<Post> getAllByOrderByViewsCountDesc();

    List<Post> findAllByTagsContainingIgnoreCase(String tag);

    List<Post> findAllByTitleIsContainingIgnoreCase(String title);

    List<Post> findAllByTitleIsContainingIgnoreCaseAndTagsContainingIgnoreCase(String title, String tag);
}
