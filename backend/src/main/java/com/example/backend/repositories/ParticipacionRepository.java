package com.example.backend.repositories;

import com.example.backend.persistence.entity.ParticipacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Participacion (Inscripciones).
 * Gestiona las inscripciones de participantes a eventos.
 */
@Repository
public interface ParticipacionRepository extends JpaRepository<ParticipacionEntity, Integer> {

    /**
     * Busca una participación por el código único.
     */
    Optional<ParticipacionEntity> findByCodigoUnicoAPI(String codigoUnicoAPI);

    /**
     * Busca todas las participaciones de un evento.
     */
    List<ParticipacionEntity> findByEvento_IdEvento(Integer eventoId);

    /**
     * Busca todas las participaciones de un participante.
     */
    List<ParticipacionEntity> findByParticipante_DocumentoIdentidad(Long documentoIdentidad);

    /**
     * Verifica si un participante ya está inscrito en un evento.
     */
    @Query("SELECT COUNT(p) > 0 FROM ParticipacionEntity p WHERE " +
            "p.participante.documentoIdentidad = :documentoIdentidad AND " +
            "p.evento.idEvento = :eventoId")
    Boolean existsByParticipanteAndEvento(@Param("documentoIdentidad") Long documentoIdentidad,
            @Param("eventoId") Integer eventoId);

    /**
     * Cuenta las inscripciones de un evento.
     */
    Long countByEvento_IdEvento(Integer eventoId);

    /**
     * Busca participaciones de un evento con check-in realizado.
     */
    @Query("SELECT p FROM ParticipacionEntity p WHERE p.evento.idEvento = :eventoId AND p.checkIn IS NOT NULL")
    List<ParticipacionEntity> findInscritosConCheckIn(@Param("eventoId") Integer eventoId);

    /**
     * Busca participaciones de un evento sin check-in.
     */
    @Query("SELECT p FROM ParticipacionEntity p WHERE p.evento.idEvento = :eventoId AND p.checkIn IS NULL")
    List<ParticipacionEntity> findInscritosSinCheckIn(@Param("eventoId") Integer eventoId);
}
