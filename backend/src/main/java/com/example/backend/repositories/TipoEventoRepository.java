package com.example.backend.repositories;

import com.example.backend.persistence.entity.TipoEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEventoRepository extends JpaRepository<TipoEventoEntity, Integer> {
}
