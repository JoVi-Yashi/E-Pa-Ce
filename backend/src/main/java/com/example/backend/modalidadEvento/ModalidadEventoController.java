package com.example.backend.modalidadEvento;

import com.example.backend.modalidadEvento.dto.ModalidadEventoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/modalidades")
public class ModalidadEventoController {

    @Autowired
    private ModalidadEventoService modalidadEventoService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('EVENTO:READ_ALL', 'EVENTO:READ_OWN', 'CONFIGURACION:READ_ALL', 'ALL:ALL')")
    public ResponseEntity<List<ModalidadEventoDTO>> getAllModalidades() {
        return ResponseEntity.ok(modalidadEventoService.getAllModalidades());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EVENTO:READ_ALL', 'EVENTO:READ_OWN', 'CONFIGURACION:READ_ALL', 'ALL:ALL')")
    public ResponseEntity<ModalidadEventoDTO> getModalidadById(@PathVariable @NonNull Integer id) {
        try {
            return ResponseEntity.ok(modalidadEventoService.getModalidadById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CONFIGURACION:CREATE', 'CONFIGURACION:MANAGE', 'ALL:ALL')")
    public ResponseEntity<ModalidadEventoDTO> createModalidad(@Valid @RequestBody ModalidadEventoDTO modalidadDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(modalidadEventoService.createModalidad(modalidadDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CONFIGURACION:UPDATE_ALL', 'CONFIGURACION:MANAGE', 'ALL:ALL')")
    public ResponseEntity<ModalidadEventoDTO> updateModalidad(@PathVariable @NonNull Integer id,
            @Valid @RequestBody ModalidadEventoDTO modalidadDTO) {
        try {
            return ResponseEntity.ok(modalidadEventoService.updateModalidad(id, modalidadDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CONFIGURACION:DELETE_ALL', 'CONFIGURACION:MANAGE', 'ALL:ALL')")
    public ResponseEntity<?> deleteModalidad(@PathVariable @NonNull Integer id) {
        try {
            modalidadEventoService.deleteModalidad(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
