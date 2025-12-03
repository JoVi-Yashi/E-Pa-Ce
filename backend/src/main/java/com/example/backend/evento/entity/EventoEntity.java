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
@Table(name = "Eventos")
public class EventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Evento", nullable = false)
    private Integer idEvento;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 30, message = "El nombre del evento no puede exceder 30 caracteres")
    @Column(name = "Nombre", nullable = false, length = 30)
    private String nombre;

    @Size(max = 150, message = "La descripción no puede exceder 150 caracteres")
    @Column(name = "Descripcion", length = 150)
    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "Fecha_Inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "Fecha_Fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "Duracion_Horas")
    private Float duracionHoras;

    @Column(name = "Aforo_Maximo")
    private Integer aforoMaximo;

    @Size(max = 10, message = "El estado no puede exceder 10 caracteres")
    @Column(name = "Estado", length = 10)
    private String estado;

    @NotNull(message = "La modalidad es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Modalidad_EventoID_ModalidadEvento", nullable = false)
    private ModalidadEventoEntity modalidadEvento;

    @NotNull(message = "El tipo de evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Tipo_EventoID_TipoEvento", nullable = false)
    private TipoEventoEntity tipoEvento;


    }
