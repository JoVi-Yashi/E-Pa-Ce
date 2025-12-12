package com.example.backend.participacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipacionResponse {
    private Integer idParticipacion;
    private LocalDateTime fechaInscripcion;
    private Long participanteDocumento;
    private String participanteNombre;
    private Integer eventoId;
    private String eventoNombre;
    private String codigoUnicoAPI;
}
