package com.example.backend.auth;

import com.example.backend.auth.dto.JwtResponse;
import com.example.backend.auth.dto.LoginRequest;
import com.example.backend.auth.dto.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<com.example.backend.shared.dto.MessageResponse> registerUser(
            @Valid @RequestBody SignupRequest signUpRequest) {
        String message = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new com.example.backend.shared.dto.MessageResponse(message));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<com.example.backend.shared.dto.MessageResponse> forgotPassword(
            @Valid @RequestBody com.example.backend.auth.dto.ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity
                .ok(new com.example.backend.shared.dto.MessageResponse("Se ha enviado un correo de recuperación."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<com.example.backend.shared.dto.MessageResponse> resetPassword(
            @Valid @RequestBody com.example.backend.auth.dto.ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getEmail(), request.getNewPassword());
        return ResponseEntity
                .ok(new com.example.backend.shared.dto.MessageResponse("Contraseña restablecida correctamente."));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken() {
        System.out.println(">>> CONTROLLER: Received refresh-token request");
        return ResponseEntity.ok(authService.refreshToken());
    }
}
