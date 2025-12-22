package com.example.backend.participacion;

import com.example.backend.participacion.dto.ParticipacionRequest;
import com.example.backend.participacion.dto.ParticipacionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/participaciones")
public class ParticipacionController {

    @Autowired
    private ParticipacionService participacionService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PARTICIPACION:READ_ALL', 'PARTICIPACION:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<List<ParticipacionResponse>> getAllParticipaciones() {
        return ResponseEntity.ok(participacionService.getAllParticipaciones());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PARTICIPACION:READ_ALL', 'PARTICIPACION:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<ParticipacionResponse> getParticipacionById(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(participacionService.getParticipacionById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('PARTICIPACION:CREATE', 'ALL:ALL')")
    public ResponseEntity<ParticipacionResponse> createParticipacion(@Valid @RequestBody ParticipacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(participacionService.createParticipacion(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PARTICIPACION:DELETE_ALL', 'PARTICIPACION:DELETE_OWN', 'ALL:ALL')")
    public ResponseEntity<Void> deleteParticipacion(@PathVariable @NonNull Integer id) {
        participacionService.deleteParticipacion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/evento/{eventoId}")
    @PreAuthorize("hasAnyAuthority('PARTICIPACION:READ_ALL', 'PARTICIPACION:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<List<ParticipacionResponse>> getByEvento(@PathVariable @NonNull Integer eventoId) {
        return ResponseEntity.ok(participacionService.getParticipacionesByEvento(eventoId));
    }

    @GetMapping("/participante/{documento}")
    @PreAuthorize("hasAnyAuthority('PARTICIPACION:READ_ALL', 'PARTICIPACION:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<List<ParticipacionResponse>> getByParticipante(@PathVariable @NonNull Long documento) {
        return ResponseEntity.ok(participacionService.getParticipacionesByParticipante(documento));
    }
}
