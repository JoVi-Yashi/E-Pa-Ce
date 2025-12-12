package com.example.backend.auditoria;

import com.example.backend.auditoria.dto.AuditoriaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MONITOR')") // Ejemplo de seguridad
    public ResponseEntity<List<AuditoriaResponse>> getAllLogs() {
        return ResponseEntity.ok(auditoriaService.getAllLogs());
    }

    @GetMapping("/participante/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaResponse>> getLogsByParticipante(@PathVariable Long id) {
        return ResponseEntity.ok(auditoriaService.getLogsByParticipante(id));
    }
}
