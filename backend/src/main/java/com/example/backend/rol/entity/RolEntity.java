package com.example.backend.rol.entity;

import jakarta.persistence.*;

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
    private Integer idRol;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "Rol_Permisos", joinColumns = @JoinColumn(name = "rol_id"))
    @Column(name = "permiso")
    private java.util.Set<String> permisos = new java.util.HashSet<>();

    @Column(name = "Nombre_Rol", length = 50, unique = true)
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
