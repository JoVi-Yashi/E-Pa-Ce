package com.example.backend.checkin;

import com.example.backend.checkin.dto.CheckInRequest;
import com.example.backend.checkin.dto.CheckInResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CHECKIN:READ_ALL', 'CHECKIN:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<List<CheckInResponse>> getAllCheckIns() {
        return ResponseEntity.ok(checkInService.getAllCheckIns());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CHECKIN:READ_ALL', 'CHECKIN:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<CheckInResponse> getCheckInById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(checkInService.getCheckInById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CHECKIN:MANUAL', 'ALL:ALL')")
    public ResponseEntity<?> createCheckIn(@Valid @RequestBody CheckInRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(checkInService.createCheckIn(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/qr")
    @PreAuthorize("hasAnyAuthority('CHECKIN:QR', 'ALL:ALL')")
    public ResponseEntity<?> checkInByQr(@RequestParam String codigo, HttpServletRequest servletRequest) {
        try {
            String ipAddress = servletRequest.getRemoteAddr();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(checkInService.realizarCheckInPorCodigo(codigo, ipAddress));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/scan")
    @PreAuthorize("hasAnyAuthority('CHECKIN:QR', 'ALL:ALL')")
    public ResponseEntity<?> checkInByScan(@RequestBody java.util.Map<String, Object> payload,
            HttpServletRequest servletRequest) {
        try {
            Long participanteId = Long.valueOf(payload.get("participanteId").toString());
            Integer eventoId = Integer.valueOf(payload.get("eventoId").toString());
            String ipAddress = servletRequest.getRemoteAddr();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(checkInService.realizarCheckIn(participanteId, eventoId, ipAddress));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Formato inválido de ID: " + e.getMessage());
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body("Datos faltantes en la solicitud");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PostMapping("/force-exit")
    @PreAuthorize("hasAnyAuthority('CHECKIN:MANUAL', 'CHECKIN:QR', 'ALL:ALL')")
    public ResponseEntity<?> forceExit(@RequestBody java.util.Map<String, Object> payload,
            HttpServletRequest servletRequest) {
        try {
            Long participanteId = Long.valueOf(payload.get("participanteId").toString());
            Integer eventoId = Integer.valueOf(payload.get("eventoId").toString());
            String ipAddress = servletRequest.getRemoteAddr();

            return ResponseEntity.ok(checkInService.realizarCheckOut(participanteId, eventoId, ipAddress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CHECKIN:DELETE_ALL', 'ALL:ALL')")
    public ResponseEntity<?> deleteCheckIn(@PathVariable Integer id) {
        try {
            checkInService.deleteCheckIn(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/evento/{eventoId}")
    @PreAuthorize("hasAnyAuthority('CHECKIN:READ_ALL', 'CHECKIN:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<List<CheckInResponse>> getCheckInsByEvento(@PathVariable Integer eventoId) {
        return ResponseEntity.ok(checkInService.getCheckInsByEvento(eventoId));
    }

    @GetMapping("/evento/{eventoId}/activos")
    @PreAuthorize("hasAnyAuthority('CHECKIN:READ_ALL', 'CHECKIN:READ_OWN', 'ALL:ALL')")
    public ResponseEntity<List<CheckInResponse>> getActiveCheckInsByEvento(@PathVariable Integer eventoId) {
        return ResponseEntity.ok(checkInService.getActiveCheckInsByEvento(eventoId));
    }
}
