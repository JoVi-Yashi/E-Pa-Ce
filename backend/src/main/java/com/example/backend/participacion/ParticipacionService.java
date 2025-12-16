package com.example.backend.participacion;

import com.example.backend.participacion.dto.ParticipacionRequest;
import com.example.backend.participacion.dto.ParticipacionResponse;
import org.springframework.lang.NonNull;
import java.util.List;

public interface ParticipacionService {
    List<ParticipacionResponse> getAllParticipaciones();

    ParticipacionResponse getParticipacionById(@NonNull Integer id);

    ParticipacionResponse createParticipacion(ParticipacionRequest request);

    void deleteParticipacion(@NonNull Integer id);

    // Add methods to find by event or participant if needed
    List<ParticipacionResponse> getParticipacionesByEvento(@NonNull Integer eventoId);

    List<ParticipacionResponse> getParticipacionesByParticipante(@NonNull Long documento);
}
