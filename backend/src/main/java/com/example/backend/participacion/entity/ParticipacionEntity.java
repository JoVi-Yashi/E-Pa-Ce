package com.example.backend.participacion.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa la participación de un usuario en un evento.
 */
@Setter
@Getter
@Entity
@Table(name = "Participacion")
public class ParticipacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Participacion", nullable = false)
    private Integer idParticipacion;

    @NotNull(message = "La fecha de inscripción es obligatoria")
    @Column(name = "Fecha_Inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @NotNull(message = "El participante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ParticipanteDocumento_Identidad", nullable = false)
    private ParticipanteEntity participante;

    @NotNull(message = "El evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EventoID_Evento", nullable = false)
    private EventoEntity evento;

    public ParticipacionEntity() {
    }
}