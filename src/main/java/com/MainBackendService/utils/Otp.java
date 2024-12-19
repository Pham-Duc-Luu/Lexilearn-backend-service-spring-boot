package com.MainBackendService.utils;

import java.security.SecureRandom;

public class Otp {
    private static final String DIGITS = "0123456789";
    private static final int OTP_LENGTH = 6;

    public static String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        return otp.toString();
    }
}
