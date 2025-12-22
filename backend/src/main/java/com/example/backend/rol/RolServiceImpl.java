package com.example.backend.rol;

import com.example.backend.rol.dto.RolDTO;
import com.example.backend.rol.entity.RolEntity;
import com.example.backend.rol.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> getAllRoles() {
        return rolRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO getRolById(@NonNull Integer id) {
        RolEntity rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
        return mapToDTO(rol);
    }

    @Override
    @Transactional
    public RolDTO createRol(RolDTO rolDTO) {
        RolEntity rol = new RolEntity();
        rol.setNombreRol(rolDTO.getNombreRol());
        rol.setPermisos(rolDTO.getPermisos());
        RolEntity saved = rolRepository.save(rol);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public RolDTO updateRol(@NonNull Integer id, RolDTO rolDTO) {
        RolEntity rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
        rol.setNombreRol(rolDTO.getNombreRol());

        // Ensure the collection is cleared and re-populated to trigger Hibernate dirty
        // checking correctly
        rol.getPermisos().clear();
        if (rolDTO.getPermisos() != null) {
            rol.getPermisos().addAll(rolDTO.getPermisos());
        }

        RolEntity updated = rolRepository.save(rol);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteRol(@NonNull Integer id) {
        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado con ID: " + id);
        }
        try {
            rolRepository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("No se puede eliminar el rol porque está asignado a uno o más usuarios.");
        }
    }

    private RolDTO mapToDTO(RolEntity entity) {
        return new RolDTO(entity.getIdRol(), entity.getNombreRol(), entity.getPermisos());
    }
}
