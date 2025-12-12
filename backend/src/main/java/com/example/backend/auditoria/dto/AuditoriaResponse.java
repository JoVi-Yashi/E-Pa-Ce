package com.example.backend.auditoria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaResponse {
    private Integer idAuditoria;
    private LocalDateTime fechaHora;
    private String ipOrigen;
    private String entidadAfectada;
    private String accion;
    private String descripcionCambio;
    private Long participanteDocumento;
    private String participanteNombre;
}
