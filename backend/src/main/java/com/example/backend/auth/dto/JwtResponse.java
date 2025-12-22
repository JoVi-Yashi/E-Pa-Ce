package com.example.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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
    private Map<String, List<String>> rolePermissions;
    private String activeRole;
    private String fotoPerfil;

    public JwtResponse(String token, Long id, String email, String nombre, String apellido, List<String> roles,
            Map<String, List<String>> rolePermissions, String activeRole, String fotoPerfil) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.roles = roles;
        this.rolePermissions = rolePermissions;
        this.activeRole = activeRole;
        this.fotoPerfil = fotoPerfil;
    }
}