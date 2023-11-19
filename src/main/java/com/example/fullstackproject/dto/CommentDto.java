package com.example.fullstackproject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommentDto {

    @NotEmpty(message = "Content should not be empty")
    private String content;

    @Min(value = 0, message = "Not negative")
    private int postId;
}
