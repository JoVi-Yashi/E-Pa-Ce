package com.example.backend.auditoria.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa la auditoría del sistema.
 */
@Setter
@Getter
@Entity
@Table(name = "Auditoria")
public class AuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Auditoria", nullable = false)
    private Integer idAuditoria;

    @NotNull(message = "La fecha y hora es obligatoria")
    @Column(name = "Fecha_Hora", nullable = false)
    private LocalDateTime fechaHora;

    @Size(max = 15, message = "La IP de origen no puede exceder 15 caracteres")
    @Column(name = "IP_Origen", length = 15)
    private String ipOrigen;

    @Size(max = 20, message = "La entidad afectada no puede exceder 20 caracteres")
    @Column(name = "Entidad_Afectada", length = 20)
    private String entidadAfectada;

    @Size(max = 20, message = "La acción no puede exceder 20 caracteres")
    @Column(name = "Accion", length = 20)
    private String accion;

    @Size(max = 100, message = "La descripción del cambio no puede exceder 100 caracteres")
    @Column(name = "Descripcion_Cambio", length = 100)
    private String descripcionCambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ParticipanteDocumento_Identidad")
    private ParticipanteEntity participante;

    public AuditoriaEntity() {
    }
}

