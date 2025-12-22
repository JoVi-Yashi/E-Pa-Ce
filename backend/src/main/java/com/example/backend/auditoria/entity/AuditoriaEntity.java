package com.example.backend.auditoria.entity;

import com.example.backend.auth.entity.ParticipanteEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/**
 * Entidad que representa la auditoría del sistema.
 */
@Setter
@Getter
@Entity
@Table(name = "auditoria")
public class AuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria", nullable = false)
    private Integer idAuditoria;

    @NotNull(message = "La fecha y hora es obligatoria")
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Size(max = 45, message = "La IP de origen no puede exceder 45 caracteres")
    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Size(max = 50, message = "La entidad afectada no puede exceder 50 caracteres")
    @Column(name = "entidad_afectada", length = 50)
    private String entidadAfectada;

    @Size(max = 50, message = "La acción no puede exceder 50 caracteres")
    @Column(name = "accion", length = 50)
    private String accion;

    @Column(name = "descripcion_cambio", columnDefinition = "TEXT")
    private String descripcionCambio;

    @Size(max = 50)
    @Column(name = "rol_usuario", length = 50)
    private String rolUsuario;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participante_id", referencedColumnName = "documento_identidad")
    private ParticipanteEntity participante;

    public AuditoriaEntity() {
    }
}
