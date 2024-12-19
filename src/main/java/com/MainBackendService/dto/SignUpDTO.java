package com.MainBackendService.dto;

import com.MainBackendService.utils.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.*;
public class SignUpDTO  {


    @NotNull(message = "Username is required")
    @NotBlank(message = "Username car not be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    public String user_name;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    public String user_email;

    @NotNull(message = "Password is required")
    @Size(min = 6)
    @StrongPassword
    public String user_password;
    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getEmail() {
        return user_email;
    }

    public void setEmail(String email) {
        this.user_email = email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


}
