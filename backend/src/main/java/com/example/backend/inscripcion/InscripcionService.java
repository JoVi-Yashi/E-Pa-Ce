package com.example.backend.inscripcion;

import com.example.backend.inscripcion.entity.InscripcionEntity;

import java.util.List;

public interface InscripcionService {
    InscripcionEntity inscribir(Long participanteId, Integer eventoId);

    InscripcionEntity checkIn(Long participanteId, Integer eventoId);

    List<InscripcionEntity> getInscripcionesPorParticipante(Long participanteId);

    List<InscripcionEntity> getInscripcionesPorEvento(Integer eventoId);

    List<InscripcionEntity> getAllInscripciones();
}
