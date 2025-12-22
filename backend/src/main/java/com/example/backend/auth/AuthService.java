package com.example.backend.auth;

import com.example.backend.auth.dto.JwtResponse;
import com.example.backend.auth.dto.LoginRequest;
import com.example.backend.auth.dto.SignupRequest;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);

    String registerUser(SignupRequest signupRequest);

    void forgotPassword(String email);

    void resetPassword(String token, String email, String newPassword);

    JwtResponse refreshToken();
}
