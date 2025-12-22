package com.example.backend.evento.entity;

import com.example.backend.modalidadEvento.entity.ModalidadEventoEntity;
import com.example.backend.tipoEvento.entity.TipoEventoEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa un evento.
 */
@Setter
@Getter
@Entity
@Table(name = "evento")
public class EventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento", nullable = false)
    private Integer idEvento;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 100, message = "El nombre del evento no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "duracion_horas")
    private Float duracionHoras;

    @Column(name = "aforo_maximo")
    private Integer aforoMaximo;

    @Column(name = "aforo_actual")
    private Integer aforoActual = 0;

    @Size(max = 20)
    @Column(name = "estado", length = 20)
    private String estado = "BORRADOR";

    @NotNull(message = "La modalidad es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modalidad_evento_id", nullable = false)
    private ModalidadEventoEntity modalidadEvento;

    @NotNull(message = "El tipo de evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_evento_id", nullable = false)
    private TipoEventoEntity tipoEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private com.example.backend.auth.entity.ParticipanteEntity creadoPor;
}
