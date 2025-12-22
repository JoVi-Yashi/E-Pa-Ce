package com.example.backend.checkin.repository;

import com.example.backend.checkin.entity.CheckInEntity;
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
     * Busca el último registro de check-in para una participación.
     */
    Optional<CheckInEntity> findTopByParticipacion_IdParticipacionOrderByFechaHoraCheckInDesc(Integer participacionId);

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

    @Query("SELECT c FROM CheckInEntity c WHERE c.metodoCheckIn = :metodo")
    List<CheckInEntity> findByMetodoCheckIn(@Param("metodo") String metodo);

    /**
     * Busca check-ins en un rango de fechas.
     */
    @Query("SELECT c FROM CheckInEntity c WHERE c.fechaHoraCheckIn BETWEEN :fechaInicio AND :fechaFin")
    List<CheckInEntity> findCheckInsByFechaRange(@Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca los registros de entrada (ENTRADA) que NO tienen una salida (SALIDA)
     * posterior para ese evento.
     * Simplificado: Obtenemos los últimos check-ins de cada participación y
     * filtramos los que son ENTRADA.
     */
    @Query("SELECT c FROM CheckInEntity c WHERE c.participacion.evento.idEvento = :eventoId " +
            "AND c.fechaHoraCheckIn = (SELECT MAX(c2.fechaHoraCheckIn) FROM CheckInEntity c2 WHERE c2.participacion.idParticipacion = c.participacion.idParticipacion) "
            +
            "AND c.tipoAccion = com.example.backend.checkin.entity.TipoAccion.ENTRADA")
    List<CheckInEntity> findActiveCheckInsByEvento(@Param("eventoId") Integer eventoId);
}
