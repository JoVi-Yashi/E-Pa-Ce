package com.example.backend.checkin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInResponse {
    private Integer idCheckIn;
    private LocalDateTime fechaHoraCheckIn;
    private String metodoCheckIn;
    private String ipCheckIn;
    private Integer participacionId;
    private String participanteNombre;
    private String eventoNombre;
    private Integer eventoId;
    private Long participanteId;
    private String tipoAccion;
    private String fotoPerfil;
}
