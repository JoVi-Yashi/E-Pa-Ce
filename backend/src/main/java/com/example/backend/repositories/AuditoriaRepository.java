package com.example.backend.repositories;

import com.example.backend.persistence.entity.AuditoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Auditoria.
 * Gestiona los logs de auditoría del sistema.
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaEntity, Integer> {

    /**
     * Busca logs por entidad afectada.
     */
    List<AuditoriaEntity> findByEntidadAfectada(String entidadAfectada);

    /**
     * Busca logs por acción.
     */
    List<AuditoriaEntity> findByAccion(String accion);

    /**
     * Busca logs de un participante específico.
     */
    List<AuditoriaEntity> findByParticipante_DocumentoIdentidad(Long documentoIdentidad);

    /**
     * Busca logs en un rango de fechas.
     */
    @Query("SELECT a FROM AuditoriaEntity a WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaHora DESC")
    List<AuditoriaEntity> findByFechaRange(@Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca logs con múltiples filtros.
     */
    @Query("SELECT a FROM AuditoriaEntity a WHERE " +
            "(:entidad IS NULL OR a.entidadAfectada = :entidad) AND " +
            "(:accion IS NULL OR a.accion = :accion) AND " +
            "(:documentoIdentidad IS NULL OR a.participante.documentoIdentidad = :documentoIdentidad) " +
            "ORDER BY a.fechaHora DESC")
    List<AuditoriaEntity> findByFilters(@Param("entidad") String entidad,
            @Param("accion") String accion,
            @Param("documentoIdentidad") Long documentoIdentidad);

    /**
     * Busca los últimos N registros de auditoría.
     */
    @Query("SELECT a FROM AuditoriaEntity a ORDER BY a.fechaHora DESC")
    List<AuditoriaEntity> findRecentLogs();
}
