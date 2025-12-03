package com.example.backend.rol.repository;

import com.example.backend.rol.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Rol.
 * Proporciona acceso a los roles del sistema.
 */
@Repository
public interface RolRepository extends JpaRepository<RolEntity, Integer> {

    /**
     * Busca un rol por su nombre.
     */
    Optional<RolEntity> findByNombreRol(String nombreRol);

    /**
     * Verifica si existe un rol con el nombre dado.
     */
    Boolean existsByNombreRol(String nombreRol);
}
