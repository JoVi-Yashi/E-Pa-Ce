package com.example.backend.evento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoResponse {
    private Integer idEvento;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Float duracionHoras;
    private Integer aforoMaximo;
    private String estado;
    private Integer modalidadEventoId;
    private String modalidadEventoNombre;
    private Integer tipoEventoId;
    private String tipoEventoNombre;
}
