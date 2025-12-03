package com.example.backend.repositories;

import com.example.backend.persistence.entity.EventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Evento.
 * Proporciona métodos de consulta para gestionar eventos.
 */
@Repository
public interface EventoRepository extends JpaRepository<EventoEntity, Integer> {

    /**
     * Busca eventos por estado.
     */
    List<EventoEntity> findByEstado(String estado);

    /**
     * Busca eventos por modalidad.
     */
    List<EventoEntity> findByModalidadEvento_IdModalidadEvento(Integer modalidadId);

    /**
     * Busca eventos por tipo.
     */
    List<EventoEntity> findByTipoEvento_IdTipoEvento(Integer tipoId);

    /**
     * Busca eventos en un rango de fechas.
     */
    @Query("SELECT e FROM EventoEntity e WHERE e.fechaInicio BETWEEN :fechaInicio AND :fechaFin")
    List<EventoEntity> findByFechaRange(@Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca eventos activos.
     */
    @Query("SELECT e FROM EventoEntity e WHERE e.estado = 'ACTIVO' ORDER BY e.fechaInicio DESC")
    List<EventoEntity> findEventosActivos();

    /**
     * Busca eventos futuros.
     */
    @Query("SELECT e FROM EventoEntity e WHERE e.fechaInicio > :now AND e.estado = 'ACTIVO'")
    List<EventoEntity> findEventosFuturos(@Param("now") LocalDateTime now);

    /**
     * Busca eventos por nombre (case-insensitive).
     */
    @Query("SELECT e FROM EventoEntity e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<EventoEntity> searchByNombre(@Param("nombre") String nombre);

    /**
     * Cuenta eventos por estado.
     */
    Long countByEstado(String estado);

    /**
     * Filtra eventos con múltiples criterios.
     */
    @Query("SELECT e FROM EventoEntity e WHERE " +
            "(:estado IS NULL OR e.estado = :estado) AND " +
            "(:modalidadId IS NULL OR e.modalidadEvento.idModalidadEvento = :modalidadId) AND " +
            "(:tipoId IS NULL OR e.tipoEvento.idTipoEvento = :tipoId)")
    List<EventoEntity> findByFilters(@Param("estado") String estado,
            @Param("modalidadId") Integer modalidadId,
            @Param("tipoId") Integer tipoId);
}
