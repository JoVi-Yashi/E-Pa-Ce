package com.example.backend.repositories;

import com.example.backend.persistence.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Short> {
    Optional<RolEntity> findByNombreRol(String nombreRol);
}
