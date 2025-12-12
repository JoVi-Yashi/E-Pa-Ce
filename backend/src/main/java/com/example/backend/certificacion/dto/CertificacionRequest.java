package com.example.backend.certificacion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CertificacionRequest {
    @NotNull(message = "El ID de la participación es obligatorio")
    private Integer participacionId;
}
