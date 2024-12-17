package com.MainBackendService.repository;

import com.MainBackendService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUserName(String userName);

}
