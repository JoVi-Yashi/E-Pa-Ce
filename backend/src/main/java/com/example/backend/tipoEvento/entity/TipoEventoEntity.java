package com.example.backend.tipoEvento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa los tipos de evento.
 */
@Setter
@Getter
@Entity
@Table(name = "Tipo_Evento")
public class TipoEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TipoEvento", nullable = false)
    private Integer idTipoEvento;

    @NotBlank(message = "El nombre del tipo de evento es obligatorio")
    @Size(max = 20, message = "El nombre del tipo de evento no puede exceder 20 caracteres")
    @Column(name = "Nombre_TipoEvento", nullable = false, length = 20)
    private String nombreTipoEvento;

    public TipoEventoEntity() {
    }

    public TipoEventoEntity(Integer idTipoEvento, String nombreTipoEvento) {
        this.idTipoEvento = idTipoEvento;
        this.nombreTipoEvento = nombreTipoEvento;
    }
}