package com.example.backend.tipoEvento.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoEventoDTO {
    private Integer idTipoEvento;

    @NotBlank(message = "El nombre del tipo de evento es obligatorio")
    private String nombreTipoEvento;
}
