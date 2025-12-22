package com.example.backend.rol.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa los roles del sistema.
 */
@Setter
@Getter
@EqualsAndHashCode(of = "idRol")
@Entity
@Table(name = "rol", uniqueConstraints = {
        @UniqueConstraint(name = "uk_rol_nombre", columnNames = "nombre_rol")
})
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol", nullable = false)
    private Integer idRol;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rol_permiso", joinColumns = @JoinColumn(name = "rol_id"))
    @Column(name = "permiso")
    private java.util.Set<String> permisos = new java.util.HashSet<>();

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Column(name = "nombre_rol", length = 50, unique = true, nullable = false)
    private String nombreRol;

    public RolEntity() {
    }

    public RolEntity(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public java.util.Set<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(java.util.Set<String> permisos) {
        this.permisos = permisos;
    }
}
