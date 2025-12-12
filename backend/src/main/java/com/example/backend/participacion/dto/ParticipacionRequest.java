package com.example.backend.participacion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParticipacionRequest {
    @NotNull(message = "El ID del evento es obligatorio")
    private Integer eventoId;

    @NotNull(message = "El documento del participante es obligatorio")
    private Long participanteDocumento;
}
