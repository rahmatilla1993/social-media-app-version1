package com.example.fullstackproject.service;

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
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Transactional
public class ImageModelService {

    private final ImageRepository imageRepository;
    private final PostRepository postRepository;
    private final Utils utils;

    @Value("${uploaded-path}")
    private String uploadPath;

    @Autowired
    public ImageModelService(ImageRepository imageRepository,
                             PostRepository postRepository, Utils utils) {
        this.imageRepository = imageRepository;
        this.postRepository = postRepository;
        this.utils = utils;
    }

    public ApiResponse uploadImageToPost(MultipartFile multipartFile, int postId) {
        User user = utils.getUser();
        Path path = Path.of(uploadPath);
        Post postFromDb = user.getPosts()
                .stream()
                .filter(post -> post.getId() == postId)
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException("Post not found"));
        ImageModel postImage = imageRepository
                .findByImagePost(postFromDb)
                .orElse(null);
        if (!ObjectUtils.isEmpty(postImage)) {
            imageRepository.delete(postImage);
            try {
                Files.delete(path.resolve(postImage.getGeneratedName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        var imageModel = new ImageModel();
        String generatedName = String.format("%s.%s", UUID.randomUUID(),
                StringUtils.getFilenameExtension(multipartFile.getOriginalFilename())
        );
        imageModel.setSize(multipartFile.getSize());
        imageModel.setMimeType(multipartFile.getContentType());
        imageModel.setOriginalFileName(multipartFile.getOriginalFilename());
        imageModel.setGeneratedName(generatedName);
        imageModel.setImagePost(postFromDb);
        try {
            Files.copy(multipartFile.getInputStream(), path.resolve(generatedName), StandardCopyOption.REPLACE_EXISTING);
            imageRepository.save(imageModel);
            return new ApiResponse("Successfully uploaded", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImageModel downloadImagePost(int postId) {
        Post post = postRepository
                .findById(postId)
                .orElseThrow(() -> new ObjectNotFoundException("Post not found"));
        return imageRepository
                .findByImagePost(post)
                .orElseThrow(() -> new ObjectNotFoundException("Image not found"));
    }

    public ApiResponse deleteImage(int imageId) {
        Path path = Path.of(uploadPath);
        ImageModel imageModel = imageRepository
                .findById(imageId)
                .orElseThrow(() -> new ObjectNotFoundException("Image not found"));
        imageRepository.delete(imageModel);
        try {
            Files.delete(path.resolve(imageModel.getGeneratedName()));
            return new ApiResponse("Image deleted", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
