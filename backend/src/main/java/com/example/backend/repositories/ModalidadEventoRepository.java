package com.example.backend.repositories;

import com.example.backend.persistence.entity.ModalidadEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Modalidad de Evento.
 */
@Repository
public interface ModalidadEventoRepository extends JpaRepository<ModalidadEventoEntity, Integer> {

    /**
     * Busca una modalidad por su nombre.
     */
    Optional<ModalidadEventoEntity> findByNombreModalidadEvento(String nombreModalidadEvento);

    /**
     * Verifica si existe una modalidad con el nombre dado.
     */
    Boolean existsByNombreModalidadEvento(String nombreModalidadEvento);
}
