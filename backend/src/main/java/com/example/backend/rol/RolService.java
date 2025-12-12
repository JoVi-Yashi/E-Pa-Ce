package com.example.backend.rol;

import com.example.backend.rol.dto.RolDTO;
import java.util.List;

public interface RolService {
    List<RolDTO> getAllRoles();

    RolDTO getRolById(Integer id);

    RolDTO createRol(RolDTO rolDTO);

    RolDTO updateRol(Integer id, RolDTO rolDTO);

    void deleteRol(Integer id);
}
