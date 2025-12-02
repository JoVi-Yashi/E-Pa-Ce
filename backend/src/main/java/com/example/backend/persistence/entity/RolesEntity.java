package com.example.backend.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa los roles del sistema.
 * Catálogo de roles disponibles para los usuarios.
 */
@Setter
@Getter
@Entity
@Table(name = "roles", indexes = {
                @Index(name = "idx_rol_nombre", columnList = "nombre")
}, uniqueConstraints = {
                @UniqueConstraint(name = "uk_rol_nombre", columnNames = "nombre")
})
public class RolesEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        private Short id;

        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(max = 50, message = "El nombre del rol no puede exceder 50 caracteres")
        @Column(name = "nombre", nullable = false, length = 50, unique = true)
        private String nombre;

        @Size(max = 20, message = "El código del rol no puede exceder 20 caracteres")
        @Column(name = "rol", length = 20)
        private String rol;

        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        @Column(name = "descripcion", columnDefinition = "TEXT")
        private String descripcion;

        public RolesEntity() {
        }

        public RolesEntity(Short id, String nombre, String rol, String descripcion) {
                this.id = id;
                this.nombre = nombre;
                this.rol = rol;
                this.descripcion = descripcion;
        }

}