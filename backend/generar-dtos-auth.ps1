# Script PowerShell para generar todos los archivos faltantes del backend
# Ejecutar desde: backend/

# ==========================================
# DTOs DE AUTENTICACIÓN
# ==========================================

# LoginRequest.java
$loginRequest = @'
package com.example.backend.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
'@

# SignupRequest.java
$signupRequest = @'
package com.example.backend.dtos.auth;

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

    @NotBlank @Email
    @Size(max = 45)
    private String email;

    @NotBlank
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private Integer rolId;  // Opcional, por defecto será INVITADO
}
'@

# JwtResponse.java
$jwtResponse = @'
package com.example.backend.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private List<String> roles;

    public JwtResponse(String token, Long id, String email, String nombre, String apellido, List<String> roles) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.roles = roles;
    }
}
'@

# ==========================================
# DTOs COMUNES
# ==========================================

# MessageResponse.java
$messageResponse = @'
package com.example.backend.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String message;
}
'@

# ErrorResponse.java
$errorResponse = @'
package com.example.backend.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private Integer status;

    public ErrorResponse(String message, String details, Integer status) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.details = details;
        this.status = status;
    }
}
'@

# ==========================================
# ESCRIBIR ARCHIVOS
# ==========================================

Write-Host "Generando DTOs..." -ForegroundColor Green

# DTOs Auth
$loginRequest | Out-File -FilePath "src\main\java\com\example\backend\dtos\auth\LoginRequest.java" -Encoding UTF8
$signupRequest | Out-File -FilePath "src\main\java\com\example\backend\dtos\auth\SignupRequest.java" -Encoding UTF8
$jwtResponse | Out-File -FilePath "src\main\java\com\example\backend\dtos\auth\JwtResponse.java" -Encoding UTF8

# DTOs Common
$messageResponse | Out-File -FilePath "src\main\java\com\example\backend\dtos\common\MessageResponse.java" -Encoding UTF8
$errorResponse | Out-File -FilePath "src\main\java\com\example\backend\dtos\common\ErrorResponse.java" -Encoding UTF8

Write-Host "✅ 5 DTOs generados exitosamente!" -ForegroundColor Green
Write-Host "Archivos creados:" -ForegroundColor Yellow
Write-Host "- LoginRequest.java"
Write-Host "- SignupRequest.java"
Write-Host "- JwtResponse.java"
Write-Host "- MessageResponse.java"
Write-Host "- ErrorResponse.java"
