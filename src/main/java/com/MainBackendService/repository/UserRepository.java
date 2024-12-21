package com.MainBackendService.repository;

import com.MainBackendService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUserEmail(String userEmail);

    // Custom query method to find a user by email
    Optional<User> findByUserEmail(String userEmail);

    Optional<User> findByUserName(String userName);

}
