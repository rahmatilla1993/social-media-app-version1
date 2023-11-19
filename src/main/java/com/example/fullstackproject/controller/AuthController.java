package com.example.fullstackproject.controller;

import com.example.fullstackproject.dto.LoginDto;
import com.example.fullstackproject.dto.RegisterDto;
import com.example.fullstackproject.exception.ObjectExistsException;
import com.example.fullstackproject.payload.response.ApiResponse;
import com.example.fullstackproject.security.JwtTokenProvider;
import com.example.fullstackproject.security.SecurityConstants;
import com.example.fullstackproject.service.UserService;
import com.example.fullstackproject.validations.ResponseErrorValidation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseErrorValidation errorValidation;

    @Autowired
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          ResponseErrorValidation errorValidation
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.errorValidation = errorValidation;
    }

    @PostMapping("/signup")
    public HttpEntity<?> registerUser(@RequestBody @Valid RegisterDto signUpRequest,
                                      BindingResult bindingResult) {
        var errors = errorValidation.mapValidationResult(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }
        userService.createUser(signUpRequest);
        return ResponseEntity.ok(
                new ApiResponse("User registered successfully", true)
        );
    }

    @PostMapping("/login")
    public HttpEntity<?> authenticateUser(@RequestBody @Valid LoginDto loginRequest,
                                          BindingResult bindingResult) {
        var errors = errorValidation.mapValidationResult(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        var authenticate =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.createToken(authenticate);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public HttpEntity<?> getAuthUser(Principal principal) {
        if (ObjectUtils.isEmpty(principal)) {
            return ResponseEntity
                    .badRequest()
                    .body("Unauthorized");
        }
        return ResponseEntity.ok(
                userService.getAuthUser(principal)
        );
    }

    @ExceptionHandler
    public HttpEntity<ApiResponse> handleLogin(BadCredentialsException e) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(e.getMessage(), false));
    }

    @ExceptionHandler
    public HttpEntity<ApiResponse> handleRegister(ObjectExistsException e) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(e.getMessage(), false));
    }
}
