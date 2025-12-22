package com.example.backend.auth.entity;

import com.example.backend.rol.entity.RolEntity;
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
import java.util.stream.Collectors;

/**
 * Entidad que representa un participante del sistema.
 * Implementa UserDetails para integración con Spring Security.
 */
@Setter
@Getter
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "participante", indexes = {
        @Index(name = "idx_participante_email", columnList = "email")
})
@EntityListeners(AuditingEntityListener.class)
public class ParticipanteEntity implements UserDetails {

    @Id
    @NotNull(message = "El documento de identidad es obligatorio")
    @Column(name = "documento_identidad", nullable = false)
    private Long documentoIdentidad;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255, message = "La contraseña no puede exceder 255 caracteres")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotNull
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "HABILITADO"; // HABILITADO, INHABILITADO

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "participante_rol", joinColumns = @JoinColumn(name = "participante_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private java.util.Set<RolEntity> roles = new java.util.HashSet<>();

    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<com.example.backend.participacion.entity.ParticipacionEntity> participaciones = new java.util.ArrayList<>();

    public ParticipanteEntity() {
    }

    public ParticipanteEntity(Long documentoIdentidad, String nombre, String apellido, String email, String password,
            java.util.Set<RolEntity> roles) {
        this.documentoIdentidad = documentoIdentidad;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        java.util.List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        for (RolEntity rol : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol())); // Add Role
            for (String permiso : rol.getPermisos()) {
                authorities.add(new SimpleGrantedAuthority(permiso)); // Add Permissions
            }
        }
        return authorities;
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
        return "HABILITADO".equalsIgnoreCase(this.estado);
    }

    public String getHighestPriorityRole() {
        if (roles == null || roles.isEmpty())
            return "INVITADO";

        java.util.List<String> roleNames = roles.stream()
                .map(RolEntity::getNombreRol)
                .collect(Collectors.toList());

        if (roleNames.contains("ADMIN"))
            return "ADMIN";
        if (roleNames.contains("OPERADOR"))
            return "OPERADOR";
        if (roleNames.contains("MONITOR"))
            return "MONITOR";
        return "INVITADO";
    }
}