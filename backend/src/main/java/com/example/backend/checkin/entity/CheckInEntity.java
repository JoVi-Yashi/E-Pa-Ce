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
@Table(name = "check_in")
public class CheckInEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_check_in", nullable = false)
    private Integer idCheckIn;

    @Size(max = 20)
    @Column(name = "metodo_check_in", length = 20)
    private String metodoCheckIn;

    @Size(max = 45)
    @Column(name = "ip_check_in", length = 45)
    private String ipCheckIn;

    @NotNull(message = "La fecha y hora de check-in es obligatoria")
    @Column(name = "fecha_hora_check_in", nullable = false)
    private LocalDateTime fechaHoraCheckIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_accion", length = 10, nullable = false)
    private TipoAccion tipoAccion = TipoAccion.ENTRADA;

    @NotNull(message = "La participación es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participacion_id", nullable = false)
    private ParticipacionEntity participacion;

    public CheckInEntity() {
    }
}
