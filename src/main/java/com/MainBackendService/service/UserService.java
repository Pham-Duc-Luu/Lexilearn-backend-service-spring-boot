package com.MainBackendService.service;

import com.MainBackendService.dto.SignUpDTO;
import com.MainBackendService.model.User;
import com.MainBackendService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public User signUp(SignUpDTO signUpDTO) {
        // Check if the username already exists
        if (userRepository.existsByUserName(signUpDTO.getUser_name())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Create a new user object
        User newUser = new User();
        newUser.setUserName(signUpDTO.getUser_name());
        newUser.setUserPassword(signUpDTO.getUser_password());  // Encrypt the password
        newUser.setUserEmail(signUpDTO.getEmail());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdateAt(LocalDateTime.now());

        // Save the user to the database
        return userRepository.save(newUser);
    }
}
