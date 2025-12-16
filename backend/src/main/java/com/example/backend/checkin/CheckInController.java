package com.example.backend.checkin;

import com.example.backend.checkin.dto.CheckInRequest;
import com.example.backend.checkin.dto.CheckInResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @GetMapping
    public ResponseEntity<List<CheckInResponse>> getAllCheckIns() {
        return ResponseEntity.ok(checkInService.getAllCheckIns());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckInResponse> getCheckInById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(checkInService.getCheckInById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createCheckIn(@Valid @RequestBody CheckInRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(checkInService.createCheckIn(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/qr")
    public ResponseEntity<?> checkInByQr(@RequestParam String codigo, HttpServletRequest servletRequest) {
        try {
            String ipAddress = servletRequest.getRemoteAddr();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(checkInService.realizarCheckInPorCodigo(codigo, ipAddress));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCheckIn(@PathVariable Integer id) {
        try {
            checkInService.deleteCheckIn(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<CheckInResponse>> getCheckInsByEvento(@PathVariable Integer eventoId) {
        return ResponseEntity.ok(checkInService.getCheckInsByEvento(eventoId));
    }
}
