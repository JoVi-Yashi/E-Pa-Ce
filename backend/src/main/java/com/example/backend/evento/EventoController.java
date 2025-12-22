package com.example.backend.evento;

import com.example.backend.evento.dto.EventoRequest;
import com.example.backend.evento.dto.EventoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('EVENTO:READ_ALL', 'EVENTO:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<List<EventoResponse>> getAllEventos() {
        List<EventoResponse> eventos = eventoService.getAllEventos();
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EVENTO:READ_ALL', 'EVENTO:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<EventoResponse> getEventoById(@PathVariable @NonNull Integer id) {
        EventoResponse evento = eventoService.getEventoById(id);
        return ResponseEntity.ok(evento);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('EVENTO:CREATE', 'ALL:ALL')")
    public ResponseEntity<EventoResponse> createEvento(@Valid @RequestBody EventoRequest eventoRequest) {
        EventoResponse newEvento = eventoService.createEvento(eventoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEvento);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EVENTO:UPDATE_OWN', 'EVENTO:UPDATE_ALL', 'ALL:ALL')")
    public ResponseEntity<EventoResponse> updateEvento(@PathVariable @NonNull Integer id,
            @Valid @RequestBody EventoRequest eventoRequest) {
        EventoResponse updatedEvento = eventoService.updateEvento(id, eventoRequest);
        return ResponseEntity.ok(updatedEvento);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EVENTO:DELETE_OWN', 'EVENTO:DELETE_ALL', 'ALL:ALL')")
    public ResponseEntity<Void> deleteEvento(@PathVariable @NonNull Integer id) {
        eventoService.deleteEvento(id);
        return ResponseEntity.noContent().build();
    }
}
