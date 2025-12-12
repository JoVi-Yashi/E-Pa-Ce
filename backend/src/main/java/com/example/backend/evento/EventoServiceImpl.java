package com.example.backend.evento;

import com.example.backend.evento.dto.EventoRequest;
import com.example.backend.evento.dto.EventoResponse;
import com.example.backend.evento.entity.EventoEntity;
import com.example.backend.evento.repository.EventoRepository;
import com.example.backend.modalidadEvento.entity.ModalidadEventoEntity;
import com.example.backend.modalidadEvento.repository.ModalidadEventoRepository;
import com.example.backend.tipoEvento.entity.TipoEventoEntity;
import com.example.backend.tipoEvento.repository.TipoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoServiceImpl implements EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ModalidadEventoRepository modalidadEventoRepository;

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventoResponse> getAllEventos() {
        return eventoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventoResponse getEventoById(@NonNull Integer id) {
        EventoEntity evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
        return mapToResponse(evento);
    }

    @Override
    @Transactional
    public EventoResponse createEvento(EventoRequest eventoRequest) {
        EventoEntity evento = new EventoEntity();
        mapToEntity(evento, eventoRequest);
        EventoEntity savedEvento = eventoRepository.save(evento);
        return mapToResponse(savedEvento);
    }

    @Override
    @Transactional
    public EventoResponse updateEvento(@NonNull Integer id, EventoRequest eventoRequest) {
        EventoEntity evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
        mapToEntity(evento, eventoRequest);
        EventoEntity updatedEvento = eventoRepository.save(evento);
        return mapToResponse(updatedEvento);
    }

    @Override
    @Transactional
    public void deleteEvento(@NonNull Integer id) {
        if (!eventoRepository.existsById(id)) {
            throw new RuntimeException("Evento no encontrado con ID: " + id);
        }
        eventoRepository.deleteById(id);
    }

    private EventoResponse mapToResponse(EventoEntity evento) {
        return new EventoResponse(
                evento.getIdEvento(),
                evento.getNombre(),
                evento.getDescripcion(),
                evento.getFechaInicio(),
                evento.getFechaFin(),
                evento.getDuracionHoras(),
                evento.getAforoMaximo(),
                evento.getEstado(),
                evento.getModalidadEvento().getIdModalidadEvento(),
                evento.getModalidadEvento().getNombreModalidadEvento(),
                evento.getTipoEvento().getIdTipoEvento(),
                evento.getTipoEvento().getNombreTipoEvento());
    }

    private void mapToEntity(EventoEntity evento, EventoRequest request) {
        evento.setNombre(request.getNombre());
        evento.setDescripcion(request.getDescripcion());
        evento.setFechaInicio(request.getFechaInicio());
        evento.setFechaFin(request.getFechaFin());
        evento.setDuracionHoras(request.getDuracionHoras());
        evento.setAforoMaximo(request.getAforoMaximo());
        evento.setEstado(request.getEstado());

        if (request.getModalidadEventoId() != null) {
            ModalidadEventoEntity modalidad = modalidadEventoRepository.findById(request.getModalidadEventoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Modalidad no encontrada con ID: " + request.getModalidadEventoId()));
            evento.setModalidadEvento(modalidad);
        }

        if (request.getTipoEventoId() != null) {
            TipoEventoEntity tipo = tipoEventoRepository.findById(request.getTipoEventoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Tipo de evento no encontrado con ID: " + request.getTipoEventoId()));
            evento.setTipoEvento(tipo);
        }
    }
}
