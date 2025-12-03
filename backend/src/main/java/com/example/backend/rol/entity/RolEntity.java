package com.example.backend.rol.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa los roles del sistema.
 */
@Setter
@Getter
@Entity
@Table(name = "Rol", uniqueConstraints = {
        @UniqueConstraint(name = "uk_rol_nombre", columnNames = "Nombre_Rol")
})
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Rol", nullable = false)
    private Short idRol;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 20, message = "El nombre del rol no puede exceder 20 caracteres")
    @Column(name = "Nombre_Rol", nullable = false, length = 20)
    private String nombreRol;

    public RolEntity() {
    }

    public RolEntity(Short idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }
}

