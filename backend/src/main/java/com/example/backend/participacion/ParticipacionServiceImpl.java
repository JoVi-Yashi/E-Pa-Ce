package com.example.backend.participacion;

import com.example.backend.auth.entity.ParticipanteEntity;
import com.example.backend.auth.repository.ParticipanteRepository;
import com.example.backend.evento.entity.EventoEntity;
import com.example.backend.evento.repository.EventoRepository;
import com.example.backend.participacion.dto.ParticipacionRequest;
import com.example.backend.participacion.dto.ParticipacionResponse;
import com.example.backend.participacion.entity.ParticipacionEntity;
import com.example.backend.participacion.repository.ParticipacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ParticipacionServiceImpl implements ParticipacionService {

    @Autowired
    private ParticipacionRepository participacionRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipacionResponse> getAllParticipaciones() {
        return participacionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ParticipacionResponse getParticipacionById(Integer id) {
        ParticipacionEntity participacion = participacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participación no encontrada con ID: " + id));
        return mapToResponse(participacion);
    }

    @Override
    @Transactional
    public ParticipacionResponse createParticipacion(ParticipacionRequest request) {
        ParticipanteEntity participante = participanteRepository.findById(request.getParticipanteDocumento())
                .orElseThrow(() -> new RuntimeException(
                        "Participante no encontrado con documento: " + request.getParticipanteDocumento()));

        EventoEntity evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + request.getEventoId()));

        ParticipacionEntity participacion = new ParticipacionEntity();
        participacion.setParticipante(participante);
        participacion.setEvento(evento);
        participacion.setFechaInscripcion(LocalDateTime.now());
        participacion.setCodigoUnicoAPI(UUID.randomUUID().toString()); // Generate a unique code

        ParticipacionEntity saved = participacionRepository.save(participacion);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteParticipacion(Integer id) {
        if (!participacionRepository.existsById(id)) {
            throw new RuntimeException("Participación no encontrada con ID: " + id);
        }
        participacionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipacionResponse> getParticipacionesByEvento(Integer eventoId) {
        return participacionRepository.findAll().stream()
                .filter(p -> p.getEvento().getIdEvento().equals(eventoId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipacionResponse> getParticipacionesByParticipante(Long documento) {
        return participacionRepository.findAll().stream()
                .filter(p -> p.getParticipante().getDocumentoIdentidad().equals(documento))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ParticipacionResponse mapToResponse(ParticipacionEntity entity) {
        return new ParticipacionResponse(
                entity.getIdParticipacion(),
                entity.getFechaInscripcion(),
                entity.getParticipante().getDocumentoIdentidad(),
                entity.getParticipante().getNombre() + " " + entity.getParticipante().getApellido(),
                entity.getEvento().getIdEvento(),
                entity.getEvento().getNombre(),
                entity.getCodigoUnicoAPI());
    }
}
