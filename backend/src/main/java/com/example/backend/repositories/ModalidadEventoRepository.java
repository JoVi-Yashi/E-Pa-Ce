package com.example.backend.repositories;

import com.example.backend.persistence.entity.ModalidadEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModalidadEventoRepository extends JpaRepository<ModalidadEventoEntity, Integer> {
}
