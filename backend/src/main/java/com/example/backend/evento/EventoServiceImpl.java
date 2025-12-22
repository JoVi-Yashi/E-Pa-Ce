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
import com.example.backend.auditoria.AuditoriaService;
import com.example.backend.config.SecurityHelper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventoServiceImpl implements EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ModalidadEventoRepository modalidadEventoRepository;

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private SecurityHelper securityHelper;

    @Override
    @Transactional(readOnly = true)
    public List<EventoResponse> getAllEventos() {
        List<EventoEntity> eventos;
        if (securityHelper.hasAuthority("EVENTO:READ_ALL") || securityHelper.hasAuthority("ALL:ALL")) {
            eventos = eventoRepository.findAll();
        } else if (securityHelper.hasAuthority("EVENTO:READ_OWN")) {
            eventos = eventoRepository.findAll().stream()
                    .filter(e -> e.getCreadoPor() != null &&
                            e.getCreadoPor().getDocumentoIdentidad()
                                    .equals(securityHelper.getCurrentUser().getDocumentoIdentidad()))
                    .collect(Collectors.toList());
        } else {
            // Default to empty if no specific READ permission is present
            return java.util.Collections.emptyList();
        }

        return eventos.stream()
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

        // Track creator
        evento.setCreadoPor(securityHelper.getCurrentUser());

        if (evento.getFechaInicio().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Error: La fecha de inicio no puede ser anterior a la fecha actual.");
        }

        if (evento.getFechaFin().isBefore(evento.getFechaInicio())) {
            throw new RuntimeException("Error: La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        @SuppressWarnings("null")
        EventoEntity savedEvento = Objects.requireNonNull(eventoRepository.save(evento));

        // Audit
        auditoriaService.registrarActividad("EVENTO", "CREATE", "Creado evento: " + savedEvento.getNombre());

        return mapToResponse(savedEvento);
    }

    @Override
    @Transactional
    public EventoResponse updateEvento(@NonNull Integer id, EventoRequest eventoRequest) {
        EventoEntity evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));

        // Security Check: OWN vs ALL
        Long ownerId = evento.getCreadoPor() != null ? evento.getCreadoPor().getDocumentoIdentidad() : null;
        if (!securityHelper.isOwnerOrHasAll(ownerId, "EVENTO:UPDATE_ALL")) {
            throw new RuntimeException("Error: No tienes permisos para modificar este evento (no eres el creador).");
        }

        mapToEntity(evento, eventoRequest);
        @SuppressWarnings("null")
        EventoEntity updatedEvento = Objects.requireNonNull(eventoRepository.save(evento));

        // Audit
        auditoriaService.registrarActividad("EVENTO", "UPDATE", "Actualizado evento: " + updatedEvento.getNombre());

        return mapToResponse(updatedEvento);
    }

    @Override
    @Transactional
    public void deleteEvento(@NonNull Integer id) {
        EventoEntity evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));

        // Security Check: OWN vs ALL
        Long ownerId = evento.getCreadoPor() != null ? evento.getCreadoPor().getDocumentoIdentidad() : null;
        if (!securityHelper.isOwnerOrHasAll(ownerId, "EVENTO:DELETE_ALL")) {
            throw new RuntimeException("Error: No tienes permisos para eliminar este evento (no eres el creador).");
        }

        // Audit
        auditoriaService.registrarActividad("EVENTO", "DELETE", "Eliminado evento con ID: " + id);

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
                evento.getAforoActual(),
                evento.getEstado(),
                evento.getModalidadEvento().getIdModalidadEvento(),
                evento.getModalidadEvento().getNombreModalidadEvento(),
                evento.getTipoEvento().getIdTipoEvento(),
                evento.getTipoEvento().getNombreTipoEvento(),
                evento.getCreadoPor() != null ? evento.getCreadoPor().getDocumentoIdentidad() : null,
                evento.getCreadoPor() != null
                        ? (evento.getCreadoPor().getNombre() + " " + evento.getCreadoPor().getApellido())
                        : "Sistema");
    }

    private void mapToEntity(EventoEntity evento, EventoRequest request) {
        evento.setNombre(request.getNombre());
        evento.setDescripcion(request.getDescripcion());
        evento.setFechaInicio(request.getFechaInicio());
        evento.setFechaFin(request.getFechaFin());
        evento.setDuracionHoras(request.getDuracionHoras());
        evento.setAforoMaximo(request.getAforoMaximo());
        evento.setEstado(request.getEstado());

        Integer modalidadId = request.getModalidadEventoId();
        if (modalidadId != null) {
            ModalidadEventoEntity modalidad = modalidadEventoRepository.findById(modalidadId)
                    .orElseThrow(() -> new RuntimeException(
                            "Modalidad no encontrada con ID: " + modalidadId));
            evento.setModalidadEvento(modalidad);
        }

        Integer tipoEventoId = request.getTipoEventoId();
        if (tipoEventoId != null) {
            TipoEventoEntity tipo = tipoEventoRepository.findById(tipoEventoId)
                    .orElseThrow(() -> new RuntimeException(
                            "Tipo de evento no encontrado con ID: " + tipoEventoId));
            evento.setTipoEvento(tipo);
        }
    }
}
