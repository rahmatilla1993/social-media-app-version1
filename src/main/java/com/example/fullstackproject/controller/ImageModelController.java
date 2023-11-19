package com.example.fullstackproject.controller;

import com.example.fullstackproject.entity.ImageModel;
import com.example.fullstackproject.exception.ObjectNotFoundException;
import com.example.fullstackproject.payload.response.ApiResponse;
import com.example.fullstackproject.service.ImageModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/image")
public class ImageModelController {

    @Value("${uploaded-path}")
    private String uploadPath;

    private final ImageModelService imageModelService;

    @Autowired
    public ImageModelController(ImageModelService imageModelService) {
        this.imageModelService = imageModelService;
    }

    @PostMapping("/{postId}/upload")
    public HttpEntity<?> uploadImageToPost(@RequestParam("file") MultipartFile file,
                                           @PathVariable("postId") int postId) {
        return ResponseEntity.ok(
                imageModelService.uploadImageToPost(file, postId)
        );
    }

    @GetMapping("/{postId}/download")
    public HttpEntity<?> downloadImagePost(@PathVariable("postId") int id) {
        ImageModel imageModel = imageModelService.downloadImagePost(id);
        Path path = Path.of(uploadPath);
        var fileSystemResource = new FileSystemResource(path.resolve(imageModel.getGeneratedName()));
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(imageModel.getMimeType()))
                .contentLength(imageModel.getSize())
                .body(fileSystemResource);
    }

    @DeleteMapping("/{imageId}/delete")
    public HttpEntity<?> deleteImage(@PathVariable("imageId") int imageId) {
        return ResponseEntity.ok(
                imageModelService.deleteImage(imageId)
        );
    }

    @ExceptionHandler
    public HttpEntity<?> handleException(ObjectNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(e.getMessage(), false));
    }
}
