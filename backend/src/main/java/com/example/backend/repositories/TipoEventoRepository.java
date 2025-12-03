package com.example.backend.repositories;

import com.example.backend.persistence.entity.TipoEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Tipo de Evento.
 */
@Repository
public interface TipoEventoRepository extends JpaRepository<TipoEventoEntity, Integer> {

    /**
     * Busca un tipo de evento por su nombre.
     */
    Optional<TipoEventoEntity> findByNombreTipoEvento(String nombreTipoEvento);

    /**
     * Verifica si existe un tipo de evento con el nombre dado.
     */
    Boolean existsByNombreTipoEvento(String nombreTipoEvento);
}
