package com.example.backend.rol;

import com.example.backend.rol.dto.RolDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROL:VIEW', 'ROL:MANAGE', 'ALL:ALL')")
    public ResponseEntity<List<RolDTO>> getAllRoles() {
        return ResponseEntity.ok(rolService.getAllRoles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROL:VIEW', 'ROL:MANAGE', 'ALL:ALL')")
    public ResponseEntity<RolDTO> getRolById(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(rolService.getRolById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROL:MANAGE', 'ALL:ALL')")
    public ResponseEntity<RolDTO> createRol(@Valid @RequestBody RolDTO rolDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.createRol(rolDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROL:MANAGE', 'ALL:ALL')")
    public ResponseEntity<RolDTO> updateRol(@PathVariable @NonNull Integer id, @Valid @RequestBody RolDTO rolDTO) {
        return ResponseEntity.ok(rolService.updateRol(id, rolDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROL:MANAGE', 'ALL:ALL')")
    public ResponseEntity<?> deleteRol(@PathVariable @NonNull Integer id) {
        try {
            rolService.deleteRol(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
