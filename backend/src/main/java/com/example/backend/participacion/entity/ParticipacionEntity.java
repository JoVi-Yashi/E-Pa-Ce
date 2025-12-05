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

    @Size(max = 40, message = "El código único API no puede exceder 40 caracteres")
    @Column(name = "Codigo_UnicoAPI", length = 40)
    private String codigoUnicoAPI;

    @NotNull(message = "El evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EventoID_Evento", nullable = false)
    private EventoEntity evento;

    @OneToOne(mappedBy = "participacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CheckInEntity checkIn;


    public ParticipacionEntity() {
    }
}