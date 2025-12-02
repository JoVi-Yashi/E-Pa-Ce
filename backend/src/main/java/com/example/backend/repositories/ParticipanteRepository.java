package com.example.backend.repositories;

import com.example.backend.persistence.entity.ParticipanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipanteRepository extends JpaRepository<ParticipanteEntity, Long> {
    Optional<ParticipanteEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
