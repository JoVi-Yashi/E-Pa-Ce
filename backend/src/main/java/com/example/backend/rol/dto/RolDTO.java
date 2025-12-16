package com.example.backend.rol.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolDTO {
    private Integer idRol;

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String nombreRol;

    private java.util.Set<String> permisos;
}
