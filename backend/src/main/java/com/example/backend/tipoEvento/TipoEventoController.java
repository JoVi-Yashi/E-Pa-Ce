package com.example.backend.tipoEvento;

import com.example.backend.tipoEvento.dto.TipoEventoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-evento")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TipoEventoController {

    @Autowired
    private TipoEventoService tipoEventoService;

    @GetMapping
    public ResponseEntity<List<TipoEventoDTO>> getAllTiposEvento() {
        return ResponseEntity.ok(tipoEventoService.getAllTiposEvento());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoEventoDTO> getTipoEventoById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(tipoEventoService.getTipoEventoById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TipoEventoDTO> createTipoEvento(@Valid @RequestBody TipoEventoDTO tipoEventoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoEventoService.createTipoEvento(tipoEventoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoEventoDTO> updateTipoEvento(@PathVariable Integer id,
            @Valid @RequestBody TipoEventoDTO tipoEventoDTO) {
        try {
            return ResponseEntity.ok(tipoEventoService.updateTipoEvento(id, tipoEventoDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTipoEvento(@PathVariable Integer id) {
        try {
            tipoEventoService.deleteTipoEvento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
