package com.example.fullstackproject.utils;

import com.example.fullstackproject.entity.User;
import com.example.fullstackproject.exception.ObjectNotFoundException;
import com.example.fullstackproject.repository.UserRepository;
import com.example.fullstackproject.security.UserSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Utils {

    private final UserRepository userRepository;

    @Autowired
    public Utils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser() {
        String email = ((UserSecurity) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUsername();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }
}
