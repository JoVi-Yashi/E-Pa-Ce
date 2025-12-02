package com.example.backend.repositories;

import com.example.backend.persistence.entity.CertificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificacionRepository extends JpaRepository<CertificacionEntity, Integer> {
}
