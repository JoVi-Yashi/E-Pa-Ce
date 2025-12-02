package com.example.backend.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entidad que representa un participante del sistema.
 * Implementa UserDetails para integración con Spring Security.
 */
@Setter
@Getter
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "Participantes", indexes = {
        @Index(name = "idx_participante_email", columnList = "Email"),
        @Index(name = "idx_participante_rol", columnList = "RolID_Rol")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_participante_email", columnNames = "Email")
})
@EntityListeners(AuditingEntityListener.class)
public class ParticipanteEntity implements UserDetails {

    @Id
    @NotNull(message = "El documento de identidad es obligatorio")
    @Column(name = "Documento_Identidad", nullable = false, precision = 15)
    private Long documentoIdentidad;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 25, message = "El nombre no puede exceder 25 caracteres")
    @Column(name = "Nombre", nullable = false, length = 25)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 25, message = "El apellido no puede exceder 25 caracteres")
    @Column(name = "Apellido", nullable = false, length = 25)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 45, message = "El email no puede exceder 45 caracteres")
    @Column(name = "Email", nullable = false, length = 45, unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 100, message = "La contraseña no puede exceder 100 caracteres")
    @Column(name = "Password", nullable = false, length = 100)
    private String password;

    @CreationTimestamp
    @Column(name = "Fecha_Creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @NotNull(message = "El rol es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RolID_Rol", nullable = false, foreignKey = @ForeignKey(name = "fk_participante_rol"))
    private RolEntity rol;

    public ParticipanteEntity() {
    }

    public ParticipanteEntity(Long documentoIdentidad, String nombre, String apellido, String email, String password,
            RolEntity rol) {
        this.documentoIdentidad = documentoIdentidad;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // ==========================================================
    // MÉTODOS REQUERIDOS POR UserDetails (Implementación)
    // ==========================================================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.rol == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.getNombreRol().toUpperCase()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
