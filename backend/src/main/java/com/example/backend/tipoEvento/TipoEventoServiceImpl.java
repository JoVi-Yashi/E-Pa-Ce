package com.example.backend.tipoEvento;

import com.example.backend.tipoEvento.dto.TipoEventoDTO;
import com.example.backend.tipoEvento.entity.TipoEventoEntity;
import com.example.backend.tipoEvento.repository.TipoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoEventoServiceImpl implements TipoEventoService {

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoEventoDTO> getAllTiposEvento() {
        return tipoEventoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TipoEventoDTO getTipoEventoById(Integer id) {
        TipoEventoEntity tipoEvento = tipoEventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de evento no encontrado con ID: " + id));
        return mapToDTO(tipoEvento);
    }

    @Override
    @Transactional
    public TipoEventoDTO createTipoEvento(TipoEventoDTO tipoEventoDTO) {
        TipoEventoEntity tipoEvento = new TipoEventoEntity();
        tipoEvento.setNombreTipoEvento(tipoEventoDTO.getNombreTipoEvento());
        TipoEventoEntity saved = tipoEventoRepository.save(tipoEvento);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public TipoEventoDTO updateTipoEvento(Integer id, TipoEventoDTO tipoEventoDTO) {
        TipoEventoEntity tipoEvento = tipoEventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de evento no encontrado con ID: " + id));
        tipoEvento.setNombreTipoEvento(tipoEventoDTO.getNombreTipoEvento());
        TipoEventoEntity updated = tipoEventoRepository.save(tipoEvento);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteTipoEvento(Integer id) {
        if (!tipoEventoRepository.existsById(id)) {
            throw new RuntimeException("Tipo de evento no encontrado con ID: " + id);
        }
        tipoEventoRepository.deleteById(id);
    }

    private TipoEventoDTO mapToDTO(TipoEventoEntity entity) {
        return new TipoEventoDTO(entity.getIdTipoEvento(), entity.getNombreTipoEvento());
    }
}
