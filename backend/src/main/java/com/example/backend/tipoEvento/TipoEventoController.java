package com.example.backend.tipoEvento;

import com.example.backend.tipoEvento.dto.TipoEventoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-evento")
public class TipoEventoController {

    @Autowired
    private TipoEventoService tipoEventoService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('EVENTO:READ_ALL', 'EVENTO:READ_OWN', 'CONFIGURACION:READ_ALL', 'ALL:ALL')")
    public ResponseEntity<List<TipoEventoDTO>> getAllTiposEvento() {
        return ResponseEntity.ok(tipoEventoService.getAllTiposEvento());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EVENTO:READ_ALL', 'EVENTO:READ_OWN', 'CONFIGURACION:READ_ALL', 'ALL:ALL')")
    public ResponseEntity<TipoEventoDTO> getTipoEventoById(@PathVariable @NonNull Integer id) {
        try {
            return ResponseEntity.ok(tipoEventoService.getTipoEventoById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CONFIGURACION:CREATE', 'CONFIGURACION:MANAGE', 'ALL:ALL')")
    public ResponseEntity<TipoEventoDTO> createTipoEvento(@Valid @RequestBody TipoEventoDTO tipoEventoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoEventoService.createTipoEvento(tipoEventoDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CONFIGURACION:UPDATE_ALL', 'CONFIGURACION:MANAGE', 'ALL:ALL')")
    public ResponseEntity<TipoEventoDTO> updateTipoEvento(@PathVariable @NonNull Integer id,
            @Valid @RequestBody TipoEventoDTO tipoEventoDTO) {
        try {
            return ResponseEntity.ok(tipoEventoService.updateTipoEvento(id, tipoEventoDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CONFIGURACION:DELETE_ALL', 'CONFIGURACION:MANAGE', 'ALL:ALL')")
    public ResponseEntity<?> deleteTipoEvento(@PathVariable @NonNull Integer id) {
        try {
            tipoEventoService.deleteTipoEvento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
