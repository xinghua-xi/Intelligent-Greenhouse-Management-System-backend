package com.smartgreenhouse.auth.repository;
import com.smartgreenhouse.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}