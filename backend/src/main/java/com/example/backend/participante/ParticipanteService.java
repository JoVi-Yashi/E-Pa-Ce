package com.example.backend.participante;

import com.example.backend.auth.entity.ParticipanteEntity;
import com.example.backend.auth.repository.ParticipanteRepository;
import com.example.backend.rol.entity.RolEntity;
import com.example.backend.rol.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParticipanteService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<ParticipanteDTO> getAllParticipantes() {
        return participanteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ParticipanteDTO getParticipanteById(Long id) {
        ParticipanteEntity participante = participanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado con id: " + id));
        return convertToDTO(participante);
    }

    public ParticipanteDTO createParticipante(ParticipanteDTO dto) {
        if (participanteRepository.existsById(dto.getDocumentoIdentidad())) {
            throw new RuntimeException("Ya existe un participante con ese documento");
        }
        if (participanteRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        ParticipanteEntity entity = new ParticipanteEntity();
        entity.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido()); // Assuming you add apellido to DTO
        entity.setEmail(dto.getEmail());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getRolId() != null) {
            // For simplicity, assuming single role ID, but entity has Set<RolEntity>
            RolEntity rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + dto.getRolId()));
            entity.setRoles(Set.of(rol));
        } else {
            RolEntity defaultRol = rolRepository.findByNombreRol("USER")
                    .orElseThrow(() -> new RuntimeException(
                            "Error: El rol por defecto 'USER' no está configurado en el sistema."));
            entity.setRoles(Set.of(defaultRol));
        }

        ParticipanteEntity saved = participanteRepository.save(entity);
        return convertToDTO(saved);
    }

    public ParticipanteDTO updateParticipante(Long id, ParticipanteDTO dto) {
        ParticipanteEntity entity = participanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado"));

        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRolId() != null) {
            RolEntity rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            entity.setRoles(Set.of(rol));
        }

        return convertToDTO(participanteRepository.save(entity));
    }

    public void deleteParticipante(Long id) {
        if (!participanteRepository.existsById(id)) {
            throw new RuntimeException("Participante no encontrado");
        }
        participanteRepository.deleteById(id);
    }

    public BulkImportResult importFromCsv(org.springframework.web.multipart.MultipartFile file) {
        BulkImportResult result = new BulkImportResult();
        result.setErrors(new java.util.ArrayList<>());

        try (com.opencsv.CSVReader reader = new com.opencsv.CSVReader(
                new java.io.InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();
            // Assuming header is row 0
            if (rows.isEmpty())
                return result;

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                result.incrementTotal();
                try {
                    if (row.length < 5)
                        throw new RuntimeException(
                                "Faltan columnas (Requerido: Doc, Nombre, Apellido, Email, Password)");

                    ParticipanteDTO dto = new ParticipanteDTO();
                    dto.setDocumentoIdentidad(Long.parseLong(row[0].trim()));
                    dto.setNombre(row[1].trim());
                    dto.setApellido(row[2].trim());
                    dto.setEmail(row[3].trim());
                    dto.setPassword(row[4].trim());

                    if (row.length > 5 && !row[5].isEmpty()) {
                        String roleName = row[5].trim();
                        Optional<RolEntity> r = rolRepository.findByNombreRol(roleName);
                        if (r.isPresent())
                            dto.setRolId(r.get().getIdRol());
                    }

                    if (dto.getPassword() == null || dto.getPassword().isEmpty())
                        dto.setPassword("123456");

                    createParticipante(dto);
                    result.incrementSuccess();
                } catch (Exception e) {
                    result.incrementFailed();
                    result.getErrors().add("Fila " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error procesando CSV: " + e.getMessage());
        }
        return result;
    }

    @lombok.Data
    public static class BulkImportResult {
        private int totalProcessed = 0;
        private int successful = 0;
        private int failed = 0;
        private List<String> errors = new java.util.ArrayList<>();

        public void incrementTotal() {
            totalProcessed++;
        }

        public void incrementSuccess() {
            successful++;
        }

        public void incrementFailed() {
            failed++;
        }
    }

    private ParticipanteDTO convertToDTO(ParticipanteEntity entity) {
        ParticipanteDTO dto = new ParticipanteDTO();
        dto.setDocumentoIdentidad(entity.getDocumentoIdentidad());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setEmail(entity.getEmail());
        if (!entity.getRoles().isEmpty()) {
            RolEntity firstRol = entity.getRoles().iterator().next();
            dto.setRolId(firstRol.getIdRol());
            dto.setRolNombre(firstRol.getNombreRol());
        }
        return dto;
    }
}
