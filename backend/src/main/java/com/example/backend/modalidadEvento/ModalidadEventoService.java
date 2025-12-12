package com.example.backend.modalidadEvento;

import com.example.backend.modalidadEvento.dto.ModalidadEventoDTO;
import java.util.List;

public interface ModalidadEventoService {
    List<ModalidadEventoDTO> getAllModalidades();

    ModalidadEventoDTO getModalidadById(Integer id);

    ModalidadEventoDTO createModalidad(ModalidadEventoDTO modalidadDTO);

    ModalidadEventoDTO updateModalidad(Integer id, ModalidadEventoDTO modalidadDTO);

    void deleteModalidad(Integer id);
}
