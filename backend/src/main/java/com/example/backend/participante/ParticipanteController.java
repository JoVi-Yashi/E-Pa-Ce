package com.example.backend.participante;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

import java.util.List;

@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    @Autowired
    private ParticipanteService participanteService;

    @GetMapping
    public ResponseEntity<List<ParticipanteDTO>> getAllParticipantes() {
        return ResponseEntity.ok(participanteService.getAllParticipantes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipanteDTO> getParticipanteById(@PathVariable @NonNull Long id) {
        try {
            return ResponseEntity.ok(participanteService.getParticipanteById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateParticipante(@PathVariable @NonNull Long id, @RequestBody ParticipanteDTO dto) {
        try {
            return ResponseEntity.ok(participanteService.updateParticipante(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParticipante(@PathVariable @NonNull Long id) {
        try {
            participanteService.deleteParticipante(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> importParticipantes(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            return ResponseEntity.ok(participanteService.importFromCsv(file));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
