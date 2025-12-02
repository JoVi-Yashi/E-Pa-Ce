package com.example.backend.repositories;

import com.example.backend.persistence.entity.ParticipacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipacionRepository extends JpaRepository<ParticipacionEntity, Integer> {
}
