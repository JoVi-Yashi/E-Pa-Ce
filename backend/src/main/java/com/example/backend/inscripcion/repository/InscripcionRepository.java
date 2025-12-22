package com.example.backend.inscripcion.repository;

import com.example.backend.inscripcion.entity.InscripcionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<InscripcionEntity, Long> {
    Optional<InscripcionEntity> findByParticipante_DocumentoIdentidadAndEvento_IdEvento(Long documentoIdentidad,
            Integer idEvento);

    List<InscripcionEntity> findByParticipante_DocumentoIdentidad(Long documentoIdentidad);

    List<InscripcionEntity> findByEvento_IdEvento(Integer idEvento);
}
