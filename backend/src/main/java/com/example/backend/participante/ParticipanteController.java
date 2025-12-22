package com.example.backend.participante;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    @Autowired
    private ParticipanteService participanteService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PARTICIPANTE:READ_ALL', 'PARTICIPANTE:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<List<ParticipanteDTO>> getAllParticipantes() {
        return ResponseEntity.ok(participanteService.getAllParticipantes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PARTICIPANTE:READ_ALL', 'PARTICIPANTE:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<ParticipanteDTO> getParticipanteById(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(participanteService.getParticipanteById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PARTICIPANTE:UPDATE_OWN', 'PARTICIPANTE:UPDATE_ALL', 'ALL:ALL')")
    public ResponseEntity<ParticipanteDTO> updateParticipante(@PathVariable @NonNull Long id,
            @Valid @RequestBody ParticipanteDTO dto) {
        return ResponseEntity.ok(participanteService.updateParticipante(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PARTICIPANTE:DELETE_OWN', 'PARTICIPANTE:DELETE_ALL', 'ALL:ALL')")
    public ResponseEntity<Void> deleteParticipante(@PathVariable @NonNull Long id) {
        participanteService.deleteParticipante(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyAuthority('PARTICIPANTE:CREATE', 'PARTICIPANTE:UPDATE_ALL', 'ALL:ALL')")
    public ResponseEntity<?> importParticipantes(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(participanteService.importFromCsv(file));
    }
}
