package com.example.backend.evento;

import com.example.backend.evento.dto.EventoRequest;
import com.example.backend.evento.dto.EventoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<EventoResponse>> getAllEventos() {
        List<EventoResponse> eventos = eventoService.getAllEventos();
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> getEventoById(@PathVariable @NonNull Integer id) {
        try {
            EventoResponse evento = eventoService.getEventoById(id);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createEvento(@Valid @RequestBody EventoRequest eventoRequest) {
        try {
            EventoResponse newEvento = eventoService.createEvento(eventoRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newEvento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvento(@PathVariable @NonNull Integer id,
            @Valid @RequestBody EventoRequest eventoRequest) {
        try {
            EventoResponse updatedEvento = eventoService.updateEvento(id, eventoRequest);
            return ResponseEntity.ok(updatedEvento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvento(@PathVariable @NonNull Integer id) {
        try {
            eventoService.deleteEvento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
