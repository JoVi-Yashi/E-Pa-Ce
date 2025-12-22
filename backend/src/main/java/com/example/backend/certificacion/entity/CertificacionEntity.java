package com.example.backend.certificacion.entity;

import com.example.backend.participacion.entity.ParticipacionEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa una certificación.
 */
@Setter
@Getter
@Entity
@Table(name = "certificacion")
public class CertificacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_certificacion", nullable = false)
    private Integer idCertificacion;

    @Column(name = "codigo_unico_api", length = 100, unique = true)
    private String codigoUnicoAPI;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    @Size(max = 255)
    @Column(name = "ruta_pdf")
    private String rutaPDF;

    @Column(name = "emitido")
    private Boolean emitido = false;

    @NotNull(message = "La participación es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participacion_id", nullable = false, unique = true)
    private ParticipacionEntity participacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emitido_por")
    private com.example.backend.auth.entity.ParticipanteEntity emitidoPor;

    public CertificacionEntity() {
    }
}
