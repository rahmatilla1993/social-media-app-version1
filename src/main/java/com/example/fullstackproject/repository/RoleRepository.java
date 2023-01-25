package com.example.fullstackproject.repository;

import com.example.fullstackproject.entity.Role;
import com.example.fullstackproject.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(Roles role);
}
