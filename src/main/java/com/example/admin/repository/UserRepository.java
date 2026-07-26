package com.example.admin.repository;

import com.example.admin.model.entity.User;
import com.example.admin.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deleted = false ORDER BY u.createdAt DESC")
    List<User> findAllActive();

    List<User> findByRoleAndDeletedFalse(UserRole role);
}
