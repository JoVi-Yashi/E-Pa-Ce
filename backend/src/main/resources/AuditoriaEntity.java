package com.example.backend.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * Entidad que representa el registro de auditoría del sistema.
 * Registra todas las acciones importantes realizadas por los usuarios.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "auditoria", indexes = {
        @Index(name = "idx_auditoria_fecha", columnList = "fecha_hora"),
        @Index(name = "idx_auditoria_usuario", columnList = "usuario_documento"),
        @Index(name = "idx_auditoria_entidad", columnList = "entidad_afectada"),
        @Index(name = "idx_auditoria_accion", columnList = "accion")
})
@EntityListeners(AuditingEntityListener.class)
public class AuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @CreatedDate
    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private OffsetDateTime fechaHora;

    @Size(max = 45, message = "La IP de origen no puede exceder 45 caracteres")
    @Column(name = "ip_origen", length = 45) // Aumentado para soportar IPv6
    private String ipOrigen;

    @NotBlank(message = "La entidad afectada es obligatoria")
    @Size(max = 50, message = "La entidad afectada no puede exceder 50 caracteres")
    @Column(name = "entidad_afectada", length = 50, nullable = false)
    private String entidadAfectada;

    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 20, message = "La acción no puede exceder 20 caracteres")
    @Column(name = "accion", length = 20, nullable = false)
    private String accion;

    @Size(max = 500, message = "La descripción del cambio no puede exceder 500 caracteres")
    @Column(name = "descripcion_cambio", length = 500)
    private String descripcionCambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_documento", foreignKey = @ForeignKey(name = "fk_auditoria_usuario"))
    private UsuarioEntity usuario;

    /**
     * Crea un registro de auditoría.
     */
    public static AuditoriaEntity crear(
            String entidad,
            String accion,
            String descripcion,
            UsuarioEntity usuario,
            String ip) {
        return AuditoriaEntity.builder()
                .entidadAfectada(entidad)
                .accion(accion)
                .descripcionCambio(descripcion)
                .usuario(usuario)
                .ipOrigen(ip)
                .build();
    }

}