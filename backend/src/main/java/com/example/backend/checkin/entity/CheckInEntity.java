package com.example.backend.checkin.entity;

import com.example.backend.participacion.entity.ParticipacionEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa el check-in de un participante.
 */
@Setter
@Getter
@Entity
@Table(name = "CheckIn")
public class CheckInEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CheckIn", nullable = false)
    private Integer idCheckIn;

    @Size(max = 10, message = "El método de check-in no puede exceder 10 caracteres")
    @Column(name = "Metodo_CheckIn", length = 10)
    private String metodoCheckIn;

    @Size(max = 15, message = "La IP de check-in no puede exceder 15 caracteres")
    @Column(name = "IPCheckIn", length = 15)
    private String ipCheckIn;

    @NotNull(message = "La fecha y hora de check-in es obligatoria")
    @Column(name = "Fecha_HoraCheckIn", nullable = false)
    private LocalDateTime fechaHoraCheckIn;

    @NotNull(message = "La participación es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ParticipacionID_Participacion", nullable = false)
    private ParticipacionEntity participacion;

    public CheckInEntity() {
    }
}
