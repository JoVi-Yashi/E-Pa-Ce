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
@Table(name = "Certificacion")
public class CertificacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Certificacion", nullable = false)
    private Integer idCertificacion;

    @Column(name = "Codigo_Unico_API", length = 50, unique = true)
    private String codigoUnicoAPI;

    @Column(name = "Fecha_Emision")
    private LocalDateTime fechaEmision;

    @Size(max = 50, message = "La ruta del PDF no puede exceder 50 caracteres")
    @Column(name = "RutaPDF", length = 50)
    private String rutaPDF;

    @Column(name = "Emitido")
    private Boolean emitido;


    @NotNull(message = "La participación es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ParticipacionID_Participacion", nullable = false)
    private ParticipacionEntity participacion;

    public CertificacionEntity() {
    }
}
