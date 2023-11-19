package com.example.fullstackproject.service;

import com.example.fullstackproject.dto.RegisterDto;
import com.example.fullstackproject.entity.User;
import com.example.fullstackproject.enums.Roles;
import com.example.fullstackproject.enums.Status;
import com.example.fullstackproject.exception.ObjectExistsException;
import com.example.fullstackproject.exception.ObjectNotFoundException;
import com.example.fullstackproject.repository.RoleRepository;
import com.example.fullstackproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
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

    private void isEmailExists(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return;
        }
        throw new ObjectExistsException("This email already exists");
    }

    @Transactional
    public void createUser(RegisterDto signUpRequest) {
        String email = signUpRequest.getEmail();
        isEmailExists(email);
        var user = new User();
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        var role = roleRepository.findByRole(Roles.ROLE_USER).orElse(null);
        assert role != null;
        user.setRoles(Set.of(role));
        user.setFullName(signUpRequest.getFullName());
        user.setEmail(email);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
    }

    public User getAuthUser(Principal principal) {
        return userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }
}
