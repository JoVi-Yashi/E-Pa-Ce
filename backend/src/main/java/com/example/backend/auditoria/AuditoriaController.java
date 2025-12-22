package com.example.backend.auditoria;

import com.example.backend.auditoria.dto.AuditoriaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('AUDITORIA:VIEW', 'ALL:ALL')")
    public ResponseEntity<List<AuditoriaResponse>> getAllLogs() {
        return ResponseEntity.ok(auditoriaService.getAllLogs());
    }

    @GetMapping("/participante/{id}")
    @PreAuthorize("hasAnyAuthority('AUDITORIA:VIEW', 'ALL:ALL')")
    public ResponseEntity<List<AuditoriaResponse>> getLogsByParticipante(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(auditoriaService.getLogsByParticipante(id));
    }

    @PostMapping("/log-role-switch")
    public ResponseEntity<Void> logRoleSwitch(@RequestBody java.util.Map<String, String> data) {
        String from = data.get("from");
        String to = data.get("to");
        auditoriaService.registrarActividad("AUTH", "UPDATE", "Cambio de rol activo: " + from + " -> " + to);
        return ResponseEntity.ok().build();
    }
}
