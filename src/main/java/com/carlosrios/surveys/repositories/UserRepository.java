package com.carlosrios.surveys.repositories;

import com.carlosrios.surveys.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// @Repository isn't necessary it comes with JpaRepository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
