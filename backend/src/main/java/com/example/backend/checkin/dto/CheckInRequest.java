package com.example.backend.checkin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckInRequest {
    @NotNull(message = "El ID de la participación es obligatorio")
    private Integer participacionId;

    @Size(max = 10, message = "El método de check-in no puede exceder 10 caracteres")
    private String metodoCheckIn;

    @Size(max = 15, message = "La IP de check-in no puede exceder 15 caracteres")
    private String ipCheckIn;

    private String codigoUnicoAPI; // Opcional, para validar si es por QR

    private String tipoAccion; // "ENTRADA" o "SALIDA"
}
