package com.example.backend.evento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventoRequest {
    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 30)
    private String nombre;

    @Size(max = 150)
    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    private Float duracionHoras;

    private Integer aforoMaximo;

    @Size(max = 10)
    private String estado;

    @NotNull(message = "La modalidad es obligatoria")
    private Integer modalidadEventoId;

    @NotNull(message = "El tipo de evento es obligatorio")
    private Integer tipoEventoId;
}
