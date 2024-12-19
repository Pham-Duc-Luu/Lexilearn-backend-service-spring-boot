package com.MainBackendService.dto;

import com.MainBackendService.utils.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VerifyOtpAndChangePasswordDto {

    @NotNull
    @NotBlank
    private String otp;
    @NotNull
    @NotBlank
    @StrongPassword
    private String password;
    @NotNull
    @NotBlank
    @Email
    private String email;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
