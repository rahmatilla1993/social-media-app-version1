package com.example.fullstackproject.security;

import com.example.fullstackproject.entity.User;
import com.example.fullstackproject.enums.Status;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserSecurity implements UserDetails {

    private User user;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getStatus().equals(Status.ACTIVE);
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus().equals(Status.ACTIVE);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getStatus().equals(Status.ACTIVE);
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().equals(Status.ACTIVE);
    }
}
