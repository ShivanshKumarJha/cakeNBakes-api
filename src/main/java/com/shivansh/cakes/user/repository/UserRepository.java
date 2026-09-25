package com.shivansh.cakes.user.repository;

import com.shivansh.cakes.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByContactNumber(String contactNumber);
    List<User> findAllByOrderByCreatedAtDesc();
}
