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
@Table(name = "Modalidad_Evento")
public class ModalidadEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ModalidadEvento", nullable = false)
    private Integer idModalidadEvento;

    @NotBlank(message = "El nombre de la modalidad es obligatorio")
    @Size(max = 20, message = "El nombre de la modalidad no puede exceder 20 caracteres")
    @Column(name = "Nombre_ModalidadEvento", nullable = false, length = 20)
    private String nombreModalidadEvento;

    public ModalidadEventoEntity() {
    }

    public ModalidadEventoEntity(Integer idModalidadEvento, String nombreModalidadEvento) {
        this.idModalidadEvento = idModalidadEvento;
        this.nombreModalidadEvento = nombreModalidadEvento;
    }
}