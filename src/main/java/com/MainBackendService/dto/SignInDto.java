package com.MainBackendService.dto;

import com.MainBackendService.utils.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SignInDto {
    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    public String user_email;

    @NotNull(message = "Password is required")
    @Size(min = 6)
    @StrongPassword
    public String user_password;


}
