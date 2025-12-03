package com.example.backend.repositories;

import com.example.backend.persistence.entity.CheckInEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad CheckIn.
 * Gestiona los registros de asistencia.
 */
@Repository
public interface CheckInRepository extends JpaRepository<CheckInEntity, Integer> {

    /**
     * Busca un check-in por ID de participación.
     */
    Optional<CheckInEntity> findByParticipacion_IdParticipacion(Integer participacionId);

    /**
     * Verifica si existe un check-in para una participación.
     */
    Boolean existsByParticipacion_IdParticipacion(Integer participacionId);

    /**
     * Busca todos los check-ins de un evento.
     */
    @Query("SELECT c FROM CheckInEntity c WHERE c.participacion.evento.idEvento = :eventoId")
    List<CheckInEntity> findCheckInsByEvento(@Param("eventoId") Integer eventoId);

    /**
     * Cuenta los check-ins realizados en un evento.
     */
    @Query("SELECT COUNT(c) FROM CheckInEntity c WHERE c.participacion.evento.idEvento = :eventoId")
    Long countCheckInsByEvento(@Param("eventoId") Integer eventoId);

    /**
     * Busca check-ins por método (QR o MANUAL).
     */
    List<CheckInEntity> findByMetodoCheckIn(String metodoCheckIn);

    /**
     * Busca check-ins en un rango de fechas.
     */
    @Query("SELECT c FROM CheckInEntity c WHERE c.fechaHoraCheckIn BETWEEN :fechaInicio AND :fechaFin")
    List<CheckInEntity> findCheckInsByFechaRange(@Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}
