package com.example.backend.participacion.entity;

import com.example.backend.checkin.entity.CheckInEntity;
import com.example.backend.evento.entity.EventoEntity;
import com.example.backend.auth.entity.ParticipanteEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa la participación de un usuario en un evento.
 */
@Setter
@Getter
@Entity
@Table(name = "participacion", uniqueConstraints = {
        @UniqueConstraint(name = "uk_participacion_evento_participante", columnNames = { "participante_id",
                "evento_id" })
})
public class ParticipacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_participacion", nullable = false)
    private Integer idParticipacion;

    @NotNull(message = "La fecha de inscripción es obligatoria")
    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @NotNull(message = "El participante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participante_id", nullable = false)
    private ParticipanteEntity participante;

    @Size(max = 100, message = "El código único API no puede exceder 100 caracteres")
    @Column(name = "codigo_unico_api", length = 100, unique = true)
    private String codigoUnicoAPI;

    @NotNull(message = "El evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoEntity evento;

    @OneToMany(mappedBy = "participacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<CheckInEntity> checkIns = new java.util.ArrayList<>();

    @OneToOne(mappedBy = "participacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private com.example.backend.certificacion.entity.CertificacionEntity certificacion;

    @Size(max = 20, message = "El método de inscripción no puede exceder 20 caracteres")
    @Column(name = "metodo_inscripcion", length = 20)
    private String metodoInscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por")
    private ParticipanteEntity registradoPor;

    public ParticipacionEntity() {
    }
}