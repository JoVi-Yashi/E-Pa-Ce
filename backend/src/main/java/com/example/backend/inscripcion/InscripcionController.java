package com.example.backend.inscripcion;

import com.example.backend.inscripcion.entity.InscripcionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @PostMapping("/inscribir")
    public ResponseEntity<InscripcionEntity> inscribir(@RequestBody Map<String, Object> payload) {
        Long participanteId = Long.valueOf(payload.get("participanteId").toString());
        Integer eventoId = Integer.valueOf(payload.get("eventoId").toString());
        return ResponseEntity.ok(inscripcionService.inscribir(participanteId, eventoId));
    }

    @PostMapping("/checkin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MONITOR')")
    public ResponseEntity<InscripcionEntity> checkIn(@RequestBody Map<String, Object> payload) {
        Long participanteId = Long.valueOf(payload.get("participanteId").toString());
        Integer eventoId = Integer.valueOf(payload.get("eventoId").toString());
        return ResponseEntity.ok(inscripcionService.checkIn(participanteId, eventoId));
    }

    @GetMapping("/participante/{id}")
    public ResponseEntity<List<InscripcionEntity>> getPorParticipante(@PathVariable Long id) {
        return ResponseEntity.ok(inscripcionService.getInscripcionesPorParticipante(id));
    }

    @GetMapping("/evento/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MONITOR')")
    public ResponseEntity<List<InscripcionEntity>> getPorEvento(@PathVariable Integer id) {
        return ResponseEntity.ok(inscripcionService.getInscripcionesPorEvento(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MONITOR')")
    public ResponseEntity<List<InscripcionEntity>> getAll() {
        return ResponseEntity.ok(inscripcionService.getAllInscripciones());
    }
}
