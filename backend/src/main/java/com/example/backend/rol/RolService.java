package com.example.backend.rol;

import com.example.backend.rol.dto.RolDTO;
import org.springframework.lang.NonNull;
import java.util.List;

public interface RolService {
    List<RolDTO> getAllRoles();

    RolDTO getRolById(@NonNull Integer id);

    RolDTO createRol(RolDTO rolDTO);

    RolDTO updateRol(@NonNull Integer id, RolDTO rolDTO);

    void deleteRol(@NonNull Integer id);
}
