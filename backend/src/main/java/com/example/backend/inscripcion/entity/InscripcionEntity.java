package com.example.backend.inscripcion.entity;

import com.example.backend.auth.entity.ParticipanteEntity;
import com.example.backend.evento.entity.EventoEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "inscripcion", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "participante_documento", "evento_id" })
})
public class InscripcionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscripcion")
    private Long idInscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participante_documento", nullable = false)
    private ParticipanteEntity participante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoEntity evento;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @Column(name = "checked_in", nullable = false)
    private boolean checkedIn = false;

    @Column(name = "fecha_check_in")
    private LocalDateTime fechaCheckIn;

    @PrePersist
    public void prePersist() {
        if (fechaInscripcion == null) {
            fechaInscripcion = LocalDateTime.now();
        }
    }
}
