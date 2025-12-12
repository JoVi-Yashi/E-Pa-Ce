package com.example.backend.modalidadEvento;

import com.example.backend.modalidadEvento.dto.ModalidadEventoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modalidades")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModalidadEventoController {

    @Autowired
    private ModalidadEventoService modalidadEventoService;

    @GetMapping
    public ResponseEntity<List<ModalidadEventoDTO>> getAllModalidades() {
        return ResponseEntity.ok(modalidadEventoService.getAllModalidades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModalidadEventoDTO> getModalidadById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(modalidadEventoService.getModalidadById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ModalidadEventoDTO> createModalidad(@Valid @RequestBody ModalidadEventoDTO modalidadDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(modalidadEventoService.createModalidad(modalidadDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModalidadEventoDTO> updateModalidad(@PathVariable Integer id,
            @Valid @RequestBody ModalidadEventoDTO modalidadDTO) {
        try {
            return ResponseEntity.ok(modalidadEventoService.updateModalidad(id, modalidadDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteModalidad(@PathVariable Integer id) {
        try {
            modalidadEventoService.deleteModalidad(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
