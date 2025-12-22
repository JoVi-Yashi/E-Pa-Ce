package com.example.backend.modalidadEvento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa la modalidad de un evento.
 */
@Setter
@Getter
@Entity
@Table(name = "modalidad_evento")
public class ModalidadEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modalidad_evento", nullable = false)
    private Integer idModalidadEvento;

    @NotBlank(message = "El nombre de la modalidad es obligatorio")
    @Size(max = 50, message = "El nombre de la modalidad no puede exceder 50 caracteres")
    @Column(name = "nombre_modalidad_evento", nullable = false, length = 50, unique = true)
    private String nombreModalidadEvento;

    public ModalidadEventoEntity() {
    }

    public ModalidadEventoEntity(Integer idModalidadEvento, String nombreModalidadEvento) {
        this.idModalidadEvento = idModalidadEvento;
        this.nombreModalidadEvento = nombreModalidadEvento;
    }
}