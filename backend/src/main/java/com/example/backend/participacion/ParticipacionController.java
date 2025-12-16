package com.example.backend.participacion;

import com.example.backend.participacion.dto.ParticipacionRequest;
import com.example.backend.participacion.dto.ParticipacionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/participaciones")
public class ParticipacionController {

    @Autowired
    private ParticipacionService participacionService;

    @GetMapping
    public ResponseEntity<List<ParticipacionResponse>> getAllParticipaciones() {
        return ResponseEntity.ok(participacionService.getAllParticipaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipacionResponse> getParticipacionById(@PathVariable @NonNull Integer id) {
        try {
            return ResponseEntity.ok(participacionService.getParticipacionById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createParticipacion(@Valid @RequestBody ParticipacionRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(participacionService.createParticipacion(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParticipacion(@PathVariable @NonNull Integer id) {
        try {
            participacionService.deleteParticipacion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<ParticipacionResponse>> getByEvento(@PathVariable @NonNull Integer eventoId) {
        return ResponseEntity.ok(participacionService.getParticipacionesByEvento(eventoId));
    }

    @GetMapping("/participante/{documento}")
    public ResponseEntity<List<ParticipacionResponse>> getByParticipante(@PathVariable @NonNull Long documento) {
        return ResponseEntity.ok(participacionService.getParticipacionesByParticipante(documento));
    }
}
