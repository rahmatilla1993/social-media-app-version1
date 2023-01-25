package com.example.fullstackproject.service;

import com.example.fullstackproject.dto.RegisterDto;
import com.example.fullstackproject.entity.Role;
import com.example.fullstackproject.entity.User;
import com.example.fullstackproject.enums.Roles;
import com.example.fullstackproject.enums.Status;
import com.example.fullstackproject.repository.RoleRepository;
import com.example.fullstackproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public void createUser(RegisterDto signUpRequest) {
        var user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        var role = roleRepository.findByRole(Roles.ROLE_USER).orElse(null);
        user.setRoles(Set.of(role));
        user.setFirstname(signUpRequest.getFirstname());
        user.setLastname(signUpRequest.getLastname());
        user.setUsername(signUpRequest.getUsername());
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
    }
}
