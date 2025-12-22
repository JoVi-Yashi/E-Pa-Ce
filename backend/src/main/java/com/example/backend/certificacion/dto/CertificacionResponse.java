package com.example.backend.certificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificacionResponse {
    private Integer idCertificacion;
    private String codigoUnicoAPI;
    private LocalDateTime fechaEmision;
    private String rutaPDF;
    private Boolean emitido;
    private Integer participacionId;
    private String participanteNombre;
    private String eventoNombre;
    private String fotoPerfil;
}
