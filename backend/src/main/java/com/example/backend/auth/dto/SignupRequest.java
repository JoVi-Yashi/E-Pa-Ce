package com.example.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotNull(message = "El documento de identidad es obligatorio")
    private Long documentoIdentidad;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 25)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 25)
    private String apellido;

    @NotBlank
    @Email
    @Size(max = 45)
    private String email;

    @NotBlank
    @Size(min = 6, message = "La contraseÃ±a debe tener al menos 6 caracteres")
    private String password;

    private java.util.Set<String> roles;
}
