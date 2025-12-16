package com.example.backend.participante;

import lombok.Data;

@Data
public class ParticipanteDTO {
    private Long documentoIdentidad;
    private String nombre;
    private String apellido;
    private String email;
    private String password; // Optional for update
    private Integer rolId;
    private String rolNombre;
}
