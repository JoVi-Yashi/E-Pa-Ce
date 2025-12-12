package com.example.backend.modalidadEvento.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModalidadEventoDTO {
    private Integer idModalidadEvento;

    @NotBlank(message = "El nombre de la modalidad es obligatorio")
    private String nombreModalidadEvento;
}
