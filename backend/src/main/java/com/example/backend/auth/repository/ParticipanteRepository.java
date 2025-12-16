package com.example.backend.auth.repository;

import com.example.backend.auth.entity.ParticipanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Participante.
 * Proporciona métodos de acceso a datos para gestionar participantes.
 */
@Repository
public interface ParticipanteRepository extends JpaRepository<ParticipanteEntity, Long> {

    /**
     * Busca un participante por su email.
     */
    Optional<ParticipanteEntity> findByEmail(String email);

    /**
     * Verifica si existe un participante con el email dado.
     */
    Boolean existsByEmail(String email);

    /**
     * Verifica si existe un participante con el documento de identidad dado.
     */
    Boolean existsByDocumentoIdentidad(Long documentoIdentidad);

    /**
     * Busca participantes por rol.
     */
    List<ParticipanteEntity> findByRoles_IdRol(Integer idRol);

    /**
     * Busca participantes cuyo nombre o apellido contenga el texto dado
     * (case-insensitive).
     */
    @Query("SELECT p FROM ParticipanteEntity p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.apellido) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ParticipanteEntity> searchByNombreOrApellido(@Param("searchTerm") String searchTerm);

    /**
     * Cuenta los participantes por rol.
     */
    @Query("SELECT COUNT(p) FROM ParticipanteEntity p JOIN p.roles r WHERE r.idRol = :rolId")
    Long countByRolId(@Param("rolId") Integer rolId);
}
