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
@Table(name = "tipo_evento")
public class TipoEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_evento", nullable = false)
    private Integer idTipoEvento;

    @NotBlank(message = "El nombre del tipo de evento es obligatorio")
    @Size(max = 50, message = "El nombre del tipo de evento no puede exceder 50 caracteres")
    @Column(name = "nombre_tipo_evento", nullable = false, length = 50, unique = true)
    private String nombreTipoEvento;

    public TipoEventoEntity() {
    }

    public TipoEventoEntity(Integer idTipoEvento, String nombreTipoEvento) {
        this.idTipoEvento = idTipoEvento;
        this.nombreTipoEvento = nombreTipoEvento;
    }
}