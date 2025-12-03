package com.example.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email vÃ¡lido")
    private String email;

    @NotBlank(message = "La contraseÃ±a es obligatoria")
    private String password;
}
