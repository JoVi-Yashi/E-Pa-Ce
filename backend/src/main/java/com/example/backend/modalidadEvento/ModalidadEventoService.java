package com.example.backend.modalidadEvento;

import com.example.backend.modalidadEvento.dto.ModalidadEventoDTO;
import org.springframework.lang.NonNull;
import java.util.List;

public interface ModalidadEventoService {
    List<ModalidadEventoDTO> getAllModalidades();

    ModalidadEventoDTO getModalidadById(@NonNull Integer id);

    ModalidadEventoDTO createModalidad(ModalidadEventoDTO modalidadDTO);

    ModalidadEventoDTO updateModalidad(@NonNull Integer id, ModalidadEventoDTO modalidadDTO);

    void deleteModalidad(@NonNull Integer id);
}
