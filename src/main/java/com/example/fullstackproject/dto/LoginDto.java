package com.example.fullstackproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDto {

        @NotEmpty(message = "Email is required")
        @Email(message = "Email is not valid")
        private String email;

        @NotEmpty(message = "Password is required")
        private String password;
}
