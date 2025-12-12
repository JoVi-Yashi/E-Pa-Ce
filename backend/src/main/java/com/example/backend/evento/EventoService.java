package com.example.backend.evento;

import com.example.backend.evento.dto.EventoRequest;
import com.example.backend.evento.dto.EventoResponse;
import org.springframework.lang.NonNull;

import java.util.List;

public interface EventoService {
    List<EventoResponse> getAllEventos();

    EventoResponse getEventoById(@NonNull Integer id);

    EventoResponse createEvento(EventoRequest eventoRequest);

    EventoResponse updateEvento(@NonNull Integer id, EventoRequest eventoRequest);

    void deleteEvento(@NonNull Integer id);
}
