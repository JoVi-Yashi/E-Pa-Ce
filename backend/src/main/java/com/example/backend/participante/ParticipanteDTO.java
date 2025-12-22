package com.example.backend.participante;

import lombok.Data;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.backend.validation.NoSqlInjection;

@Data
public class ParticipanteDTO {
    private Long documentoIdentidad;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 25)
    @NoSqlInjection
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 25)
    @NoSqlInjection
    private String apellido;

    @NotBlank
    @Email
    @Size(max = 45)
    @NoSqlInjection
    private String email;

    private String fotoPerfil; // Base64 profile image

    @Size(max = 128)
    private String password; // Optional for update
    private String oldPassword; // Required for password change
    private Integer rolId; // For backward compatibility (single role)
    private String rolNombre; // For backward compatibility
    private List<Integer> rolesIds; // Multiple roles by ID
    private List<String> rolesNombres; // Multiple role names for display
    private String estado; // HABILITADO, INHABILITADO
}
