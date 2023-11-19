package com.example.fullstackproject.repository;

import com.example.fullstackproject.entity.ImageModel;
import com.example.fullstackproject.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageModel, Integer> {

    Optional<ImageModel> findByImagePost(Post post);
}
