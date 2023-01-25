package com.example.fullstackproject.controller;

import com.example.fullstackproject.dto.LoginDto;
import com.example.fullstackproject.dto.RegisterDto;
import com.example.fullstackproject.security.JwtTokenProvider;
import com.example.fullstackproject.security.SecurityConstants;
import com.example.fullstackproject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signup")
    public HttpEntity<?> registerUser(@RequestBody @Valid RegisterDto signUpRequest,
                                      BindingResult bindingResult) {

        userService.createUser(signUpRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/signin")
    public HttpEntity<?> authenticateUser(@RequestBody @Valid LoginDto loginRequest,
                                          BindingResult bindingResult) {

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
}
