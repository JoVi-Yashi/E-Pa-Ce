package com.example.backend.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@NamedQuery(name = "User.findByEmail", query = "select u from User where u.email=email")

/**
 * Entidad que representa un usuario del sistema.
 * Implementa UserDetails para integración con Spring Security.
 */
@Setter
@Getter
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "usuarios", indexes = {
        @Index(name = "idx_usuario_email", columnList = "email"),
        @Index(name = "idx_usuario_rol", columnList = "rol_id"),
        @Index(name = "idx_usuario_activo", columnList = "activo")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
})
@EntityListeners(AuditingEntityListener.class)
public class UsuarioEntity implements UserDetails {

    @Id
    @NotBlank(message = "El documento de identidad es obligatorio")
    @Size(max = 15, message = "El documento de identidad no puede exceder 15 caracteres")
    @Column(name = "documento_identidad", nullable = false, length = 15)
    private String documentoIdentidad;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotNull(message = "El rol es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false, foreignKey = @ForeignKey(name = "fk_usuario_rol"))
    private RolesEntity rol;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private OffsetDateTime fechaRegistro;

    @ColumnDefault("true")
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public UsuarioEntity() {
    }

    public UsuarioEntity(String documentoIdentidad, String nombre, String apellido, String email, String passwordHash,
            RolesEntity rol, OffsetDateTime fechaRegistro, Boolean activo) {
        this.documentoIdentidad = documentoIdentidad;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
    }

    // Getters and Setters

    // ==========================================================
    // MÉTODOS REQUERIDOS POR UserDetails (Implementación)
    // ==========================================================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Devuelve el rol del usuario como una autoridad de seguridad
        if (this.rol == null) {
            return List.of(); // Si no tiene rol, devuelve una lista vacía
        }
        // Prefijo "ROLE_" es una convención de Spring Security
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.getNombre().toUpperCase()));
    }

    @Override
    public String getPassword() {
        // Retorna el hash de la contraseña almacenada en la DB
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        // Spring Security usa 'username' como el identificador principal.
        // En un API con JWT, esto suele ser el EMAIL.
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Lógica para cuentas expiradas
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Lógica para cuentas bloqueadas
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Lógica para credenciales expiradas
    }

    @Override
    public boolean isEnabled() {
        // Usamos el campo 'activo' de tu modelo
        return this.activo != null && this.activo;
    }
}
