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
import com.example.backend.config.SecurityHelper;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@Service
@Transactional
public class ParticipanteService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.example.backend.auditoria.AuditoriaService auditoriaService;

    @Autowired
    private SecurityHelper securityHelper;

    @Transactional(readOnly = true)
    public List<ParticipanteDTO> getAllParticipantes() {
        List<ParticipanteEntity> participantes;
        if (securityHelper.hasAuthority("PARTICIPANTE:READ_ALL") || securityHelper.hasAuthority("ALL:ALL")) {
            participantes = participanteRepository.findAll();
        } else if (securityHelper.hasAuthority("PARTICIPANTE:READ_OWN")) {
            // Can only see themselves
            ParticipanteEntity current = securityHelper.getCurrentUser();
            participantes = current != null ? List.of(current) : List.of();
        } else {
            return List.of();
        }

        List<ParticipanteDTO> dtos = participantes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return dtos;
    }

    @Transactional(readOnly = true)
    public ParticipanteDTO getParticipanteById(Long id) {
        ParticipanteEntity participante = participanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado con id: " + id));
        return convertToDTO(participante);
    }

    public ParticipanteDTO createParticipante(ParticipanteDTO dto) {
        if (participanteRepository.existsById(dto.getDocumentoIdentidad())) {
            throw new RuntimeException("Ya existe un participante con ese documento");
        }
        String email = dto.getEmail() != null ? dto.getEmail().toLowerCase() : null;
        if (participanteRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        ParticipanteEntity entity = new ParticipanteEntity();
        entity.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido()); // Assuming you add apellido to DTO
        entity.setEmail(email);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getEstado() != null) {
            entity.setEstado(dto.getEstado());
        } else {
            entity.setEstado("HABILITADO");
        }

        // Initialize roles set to avoid null pointer
        entity.setRoles(new java.util.HashSet<>());

        if (dto.getRolesIds() != null && !dto.getRolesIds().isEmpty()) {
            Set<RolEntity> roles = new java.util.HashSet<>();
            for (Integer roleId : dto.getRolesIds()) {
                RolEntity rol = rolRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));
                roles.add(rol);
            }
            entity.getRoles().addAll(roles);
        } else if (dto.getRolId() != null) {
            RolEntity rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + dto.getRolId()));
            entity.getRoles().add(rol);
        } else {
            RolEntity defaultRol = rolRepository.findByNombreRol("MONITOR")
                    .orElseThrow(() -> new RuntimeException(
                            "Error: El rol por defecto 'MONITOR' no está configurado en el sistema."));
            entity.getRoles().add(defaultRol);
        }

        ParticipanteEntity saved = participanteRepository.save(entity);

        // Audit
        auditoriaService.registrarActividad("PARTICIPANTE", "CREATE",
                "Creado participante: " + saved.getNombre() + " (ID: " + saved.getDocumentoIdentidad() + ")");

        return convertToDTO(saved);
    }

    public ParticipanteDTO updateParticipante(Long id, ParticipanteDTO dto) {
        System.out.println("DEBUG: Updating user " + id);
        System.out.println("DEBUG: Received rolesIds: " + dto.getRolesIds());
        System.out.println("DEBUG: Received rolId: " + dto.getRolId());

        ParticipanteEntity entity = participanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado"));

        // Security Check: OWN vs ALL
        if (!securityHelper.isOwnerOrHasAll(id, "PARTICIPANTE:UPDATE_ALL")) {
            throw new RuntimeException("Error: No tienes permisos para modificar este perfil (no es el tuyo).");
        }

        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        String email = dto.getEmail() != null ? dto.getEmail().toLowerCase() : null;
        entity.setEmail(email);
        entity.setFotoPerfil(dto.getFotoPerfil());

        if (dto.getEstado() != null) {
            entity.setEstado(dto.getEstado());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            // Check if current password is provided and matches
            if (dto.getOldPassword() == null || dto.getOldPassword().isEmpty()) {
                throw new RuntimeException("Debe proporcionar su contraseña actual para cambiarla.");
            }
            if (!passwordEncoder.matches(dto.getOldPassword(), entity.getPassword())) {
                throw new RuntimeException("La contraseña actual es incorrecta.");
            }
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Handle multiple roles - Explicitly check if rolesIds is present
        if (dto.getRolesIds() != null && !dto.getRolesIds().isEmpty()) {
            System.out.println("DEBUG: Processing multiple roles: " + dto.getRolesIds());
            Set<RolEntity> newRoles = new java.util.HashSet<>();
            for (Integer roleId : dto.getRolesIds()) {
                RolEntity rol = rolRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));
                newRoles.add(rol);
            }

            // Constraint: User cannot remove roles from themselves, only add more
            ParticipanteEntity currentUser = getCurrentUser();
            if (currentUser != null && currentUser.getDocumentoIdentidad().equals(id)) {
                Set<Integer> currentRoleIds = entity.getRoles().stream()
                        .map(RolEntity::getIdRol)
                        .collect(Collectors.toSet());

                Set<Integer> requestedRoleIds = new java.util.HashSet<>(dto.getRolesIds());

                // If any current role is missing in requested roles, throw error
                if (!requestedRoleIds.containsAll(currentRoleIds)) {
                    throw new RuntimeException("No puedes quitarte roles a ti mismo. Solo puedes añadirte más.");
                }
            }

            // Safe update of collection for Hibernate
            entity.getRoles().clear();
            entity.getRoles().addAll(newRoles);

            System.out.println("DEBUG: Roles set to entity: " + newRoles.size());
        } else if (dto.getRolId() != null) {
            // Backward compatibility: single role
            System.out.println("DEBUG: Processing single role: " + dto.getRolId());
            RolEntity rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            // Check constraint for single role too
            ParticipanteEntity currentUser = getCurrentUser();
            if (currentUser != null && currentUser.getDocumentoIdentidad().equals(id)) {
                if (entity.getRoles().size() > 1 ||
                        (entity.getRoles().size() == 1
                                && !entity.getRoles().iterator().next().getIdRol().equals(dto.getRolId()))) {
                    throw new RuntimeException(
                            "No puedes quitarte o cambiarte roles a ti mismo de esta forma. Solo puedes añadirte más.");
                }
            }

            entity.getRoles().clear();
            entity.getRoles().add(rol);
        }

        ParticipanteEntity saved = participanteRepository.save(entity);
        System.out.println("DEBUG: Saved entity roles count: " + saved.getRoles().size());

        // Audit
        auditoriaService.registrarActividad("PARTICIPANTE", "UPDATE",
                "Actualizado participante: " + saved.getNombre() + " (ID: " + saved.getDocumentoIdentidad() + ")");

        return convertToDTO(saved);
    }

    public void deleteParticipante(Long id) {
        ParticipanteEntity currentUser = securityHelper.getCurrentUser();
        if (currentUser != null && currentUser.getDocumentoIdentidad().equals(id)) {
            throw new RuntimeException("No puedes eliminar tu propia cuenta.");
        }

        // Security Check: OWN vs ALL
        if (!securityHelper.isOwnerOrHasAll(id, "PARTICIPANTE:DELETE_ALL")) {
            throw new RuntimeException("Error: No tienes permisos para eliminar este perfil (no es el tuyo).");
        }

        if (!participanteRepository.existsById(id)) {
            throw new RuntimeException("Participante no encontrado");
        }

        // De-link from audit logs to allow deletion
        auditoriaService.desvincularUsuario(id);

        // Audit
        auditoriaService.registrarActividad("PARTICIPANTE", "DELETE", "Eliminado participante con ID: " + id);

        participanteRepository.deleteById(id);
    }

    private ParticipanteEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof ParticipanteEntity) {
            return (ParticipanteEntity) principal;
        }
        return null;
    }

    public BulkImportResult importFromCsv(org.springframework.web.multipart.MultipartFile file) {
        BulkImportResult result = new BulkImportResult();
        result.setErrors(new java.util.ArrayList<>());

        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(file.getInputStream()))) {
            String firstLine = br.readLine();
            if (firstLine == null)
                return result;

            char separator = ',';
            if (firstLine.contains(";"))
                separator = ';';

            com.opencsv.CSVParser parser = new com.opencsv.CSVParserBuilder().withSeparator(separator).build();
            com.opencsv.CSVReader reader = new com.opencsv.CSVReaderBuilder(
                    new java.io.StringReader(firstLine + "\n" + br.lines().collect(Collectors.joining("\n"))))
                    .withCSVParser(parser)
                    .build();

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
                    String email = row[3].trim().toLowerCase();
                    dto.setEmail(email);
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
        dto.setFotoPerfil(entity.getFotoPerfil());
        dto.setEstado(entity.getEstado());

        System.out.println("DEBUG: Converting user " + entity.getDocumentoIdentidad());

        if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
            System.out.println("DEBUG: Entity has " + entity.getRoles().size() + " roles.");

            // Sort roles to ensure deterministic order (lowest ID first)
            List<RolEntity> sortedRoles = entity.getRoles().stream()
                    .sorted((r1, r2) -> r1.getIdRol().compareTo(r2.getIdRol()))
                    .collect(Collectors.toList());

            // Set first role for backward compatibility (Deterministic now)
            RolEntity firstRol = sortedRoles.get(0);
            dto.setRolId(firstRol.getIdRol());
            dto.setRolNombre(firstRol.getNombreRol());

            // Set all roles
            List<Integer> rolesIds = sortedRoles.stream().map(RolEntity::getIdRol).collect(Collectors.toList());
            List<String> rolesNombres = sortedRoles.stream().map(RolEntity::getNombreRol).collect(Collectors.toList());

            dto.setRolesIds(rolesIds);
            dto.setRolesNombres(rolesNombres);

            System.out.println("DEBUG: DTO rolesIds: " + rolesIds);
        } else {
            System.out.println("DEBUG: Entity has NO roles.");
        }
        return dto;
    }
}
