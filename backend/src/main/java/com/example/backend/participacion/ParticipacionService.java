package com.example.backend.participacion;

import com.example.backend.participacion.dto.ParticipacionRequest;
import com.example.backend.participacion.dto.ParticipacionResponse;
import java.util.List;

public interface ParticipacionService {
    List<ParticipacionResponse> getAllParticipaciones();

    ParticipacionResponse getParticipacionById(Integer id);

    ParticipacionResponse createParticipacion(ParticipacionRequest request);

    void deleteParticipacion(Integer id);

    // Add methods to find by event or participant if needed
    List<ParticipacionResponse> getParticipacionesByEvento(Integer eventoId);

    List<ParticipacionResponse> getParticipacionesByParticipante(Long documento);
}
