package com.c1b1.v2.studyone.studyone.repository;

import com.c1b1.v2.studyone.studyone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}