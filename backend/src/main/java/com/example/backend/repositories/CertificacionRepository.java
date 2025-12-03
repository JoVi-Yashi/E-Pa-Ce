package com.example.backend.repositories;

import com.example.backend.persistence.entity.CertificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Certificacion.
 * Gestiona los certificados digitales.
 */
@Repository
public interface CertificacionRepository extends JpaRepository<CertificacionEntity, Integer> {

    /**
     * Busca un certificado por código único (para API pública de verificación).
     */
    Optional<CertificacionEntity> findByCodigoUnicoAPI(String codigoUnicoAPI);

    /**
     * Busca un certificado por ID de participación.
     */
    Optional<CertificacionEntity> findByParticipacion_IdParticipacion(Integer participacionId);

    /**
     * Verifica si existe un certificado para una participación.
     */
    Boolean existsByParticipacion_IdParticipacion(Integer participacionId);

    /**
     * Busca todos los certificados emitidos.
     */
    List<CertificacionEntity> findByEmitido(Boolean emitido);

    /**
     * Busca todos los certificados de un evento.
     */
    @Query("SELECT c FROM CertificacionEntity c WHERE c.participacion.evento.idEvento = :eventoId AND c.emitido = true")
    List<CertificacionEntity> findCertificadosEmitidosPorEvento(@Param("eventoId") Integer eventoId);

    /**
     * Busca certificados de un participante.
     */
    @Query("SELECT c FROM CertificacionEntity c WHERE c.participacion.participante.documentoIdentidad = :documentoIdentidad AND c.emitido = true")
    List<CertificacionEntity> findCertificadosByParticipante(@Param("documentoIdentidad") Long documentoIdentidad);

    /**
     * Cuenta certificados emitidos.
     */
    Long countByEmitido(Boolean emitido);
}
