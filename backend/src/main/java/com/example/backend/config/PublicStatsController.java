package com.example.backend.config;

import com.example.backend.evento.EventoService;
import com.example.backend.participante.ParticipanteService;
import com.example.backend.tipoEvento.TipoEventoService;
import com.example.backend.certificacion.CertificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/stats")
public class PublicStatsController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private ParticipanteService participanteService;

    @Autowired
    private TipoEventoService tipoEventoService;

    @Autowired
    private CertificacionService certificacionService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPublicStats() {
        Map<String, Object> stats = new HashMap<>();

        // Use services to get counts. If services don't have getCount, use .size() of
        // getAll
        // In a real app we'd add .count() to repos, but let's see what's available
        try {
            stats.put("eventos", eventoService.getAllEventos().size());
            stats.put("participantes", participanteService.getAllParticipantes().size());
            stats.put("tiposEvento", tipoEventoService.getAllTiposEvento().size());
            stats.put("certificaciones", certificacionService.getAllCertificaciones().size());
        } catch (Exception e) {
            stats.put("error", "Error loading stats: " + e.getMessage());
            e.printStackTrace(); // Still print it to backend console
            // Default to 0 or similar to avoid "carton" fake numbers
            stats.put("eventos", 0);
            stats.put("participantes", 0);
            stats.put("tiposEvento", 0);
            stats.put("certificaciones", 0);
        }

        return ResponseEntity.ok(stats);
    }
}
