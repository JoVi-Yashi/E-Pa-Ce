package com.example.backend.modalidadEvento;

import com.example.backend.modalidadEvento.dto.ModalidadEventoDTO;
import com.example.backend.modalidadEvento.entity.ModalidadEventoEntity;
import com.example.backend.modalidadEvento.repository.ModalidadEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModalidadEventoServiceImpl implements ModalidadEventoService {

    @Autowired
    private ModalidadEventoRepository modalidadEventoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ModalidadEventoDTO> getAllModalidades() {
        return modalidadEventoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ModalidadEventoDTO getModalidadById(@NonNull Integer id) {
        ModalidadEventoEntity modalidad = modalidadEventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada con ID: " + id));
        return mapToDTO(modalidad);
    }

    @Override
    @Transactional
    public ModalidadEventoDTO createModalidad(ModalidadEventoDTO modalidadDTO) {
        ModalidadEventoEntity modalidad = new ModalidadEventoEntity();
        modalidad.setNombreModalidadEvento(modalidadDTO.getNombreModalidadEvento());
        ModalidadEventoEntity saved = modalidadEventoRepository.save(modalidad);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public ModalidadEventoDTO updateModalidad(@NonNull Integer id, ModalidadEventoDTO modalidadDTO) {
        ModalidadEventoEntity modalidad = modalidadEventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada con ID: " + id));
        modalidad.setNombreModalidadEvento(modalidadDTO.getNombreModalidadEvento());
        ModalidadEventoEntity updated = modalidadEventoRepository.save(modalidad);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteModalidad(@NonNull Integer id) {
        if (!modalidadEventoRepository.existsById(id)) {
            throw new RuntimeException("Modalidad no encontrada con ID: " + id);
        }
        modalidadEventoRepository.deleteById(id);
    }

    private ModalidadEventoDTO mapToDTO(ModalidadEventoEntity entity) {
        return new ModalidadEventoDTO(entity.getIdModalidadEvento(), entity.getNombreModalidadEvento());
    }
}
