package com.example.backend.certificacion;

import com.example.backend.certificacion.dto.CertificacionRequest;
import com.example.backend.certificacion.dto.CertificacionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificaciones")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CertificacionController {

    @Autowired
    private CertificacionService certificacionService;

    @GetMapping
    public ResponseEntity<List<CertificacionResponse>> getAllCertificaciones() {
        return ResponseEntity.ok(certificacionService.getAllCertificaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificacionResponse> getCertificacionById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(certificacionService.getCertificacionById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/verificar/{codigo}")
    public ResponseEntity<CertificacionResponse> getCertificacionByCodigo(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(certificacionService.getCertificacionByCodigo(codigo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/emitir")
    public ResponseEntity<?> emitirCertificacion(@Valid @RequestBody CertificacionRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(certificacionService.emitirCertificacion(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCertificacion(@PathVariable Integer id) {
        try {
            certificacionService.deleteCertificacion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<CertificacionResponse>> getByEvento(@PathVariable Integer eventoId) {
        return ResponseEntity.ok(certificacionService.getCertificacionesByEvento(eventoId));
    }

    @GetMapping("/participante/{documento}")
    public ResponseEntity<List<CertificacionResponse>> getByParticipante(@PathVariable Long documento) {
        return ResponseEntity.ok(certificacionService.getCertificacionesByParticipante(documento));
    }
}
