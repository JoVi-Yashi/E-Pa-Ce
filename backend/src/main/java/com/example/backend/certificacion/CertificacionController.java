package com.example.backend.certificacion;

import com.example.backend.certificacion.dto.CertificacionRequest;
import com.example.backend.certificacion.dto.CertificacionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/certificaciones")
public class CertificacionController {

    @Autowired
    private CertificacionService certificacionService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CERTIFICADO:READ_ALL', 'CERTIFICADO:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<List<CertificacionResponse>> getAllCertificaciones() {
        return ResponseEntity.ok(certificacionService.getAllCertificaciones());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CERTIFICADO:READ_ALL', 'CERTIFICADO:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<CertificacionResponse> getCertificacionById(@PathVariable @NonNull Integer id) {
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
    @PreAuthorize("hasAnyAuthority('CERTIFICADO:EMITIR', 'ALL:ALL')")
    public ResponseEntity<?> emitirCertificacion(@Valid @RequestBody CertificacionRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(certificacionService.emitirCertificacion(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CERTIFICADO:DELETE_ALL', 'ALL:ALL')")
    public ResponseEntity<?> deleteCertificacion(@PathVariable @NonNull Integer id) {
        try {
            certificacionService.deleteCertificacion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<CertificacionResponse>> getByEvento(@PathVariable @NonNull Integer eventoId) {
        return ResponseEntity.ok(certificacionService.getCertificacionesByEvento(eventoId));
    }

    @GetMapping("/participante/{documento}")
    public ResponseEntity<List<CertificacionResponse>> getByParticipante(@PathVariable @NonNull Long documento) {
        return ResponseEntity.ok(certificacionService.getCertificacionesByParticipante(documento));
    }

    @GetMapping("/descargar/{codigo}")
    public ResponseEntity<?> descargarPDF(@PathVariable String codigo) {
        try {
            CertificacionResponse cert = certificacionService.getCertificacionByCodigo(codigo);

            if (cert.getRutaPDF() == null || cert.getRutaPDF().isEmpty()) {
                return ResponseEntity.badRequest().body("El certificado no tiene PDF generado");
            }

            // Extraer el nombre del archivo de la ruta (ej: /pdfs/cert_123.pdf ->
            // cert_123.pdf)
            String fileName = cert.getRutaPDF().substring(cert.getRutaPDF().lastIndexOf('/') + 1);
            java.io.File pdfFile = new java.io.File("pdfs", fileName);

            if (!pdfFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Archivo PDF no encontrado en el servidor");
            }

            // Leer el archivo como bytes
            byte[] pdfBytes = java.nio.file.Files.readAllBytes(pdfFile.toPath());

            // Retornar el PDF con headers apropiados
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"Certificado_" + codigo + ".pdf\"")
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE,
                            org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
                    .body(pdfBytes);

        } catch (java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al leer el archivo PDF: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Certificado no encontrado con código: " + codigo);
        }
    }
}
