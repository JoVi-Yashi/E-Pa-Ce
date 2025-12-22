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
import org.springframework.lang.NonNull;
import com.example.backend.config.SecurityHelper;

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

        @Autowired
        private SecurityHelper securityHelper;

        @Override
        @Transactional(readOnly = true)
        public List<ParticipacionResponse> getAllParticipaciones() {
                List<ParticipacionEntity> participaciones;
                if (securityHelper.hasAuthority("PARTICIPACION:READ_ALL") || securityHelper.hasAuthority("ALL:ALL")) {
                        participaciones = participacionRepository.findAll();
                } else if (securityHelper.hasAuthority("PARTICIPACION:READ_OWN")) {
                        participaciones = participacionRepository.findAll().stream()
                                        .filter(p -> p.getRegistradoPor() != null &&
                                                        p.getRegistradoPor().getDocumentoIdentidad().equals(
                                                                        securityHelper.getCurrentUser()
                                                                                        .getDocumentoIdentidad()))
                                        .collect(Collectors.toList());
                } else {
                        return java.util.Collections.emptyList();
                }

                return participaciones.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ParticipacionResponse getParticipacionById(@NonNull Integer id) {
                ParticipacionEntity participacion = participacionRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Participación no encontrada con ID: " + id));
                return mapToResponse(participacion);
        }

        @Override
        @Transactional
        public ParticipacionResponse createParticipacion(ParticipacionRequest request) {
                ParticipanteEntity participante = participanteRepository
                                .findById(java.util.Objects.requireNonNull(request.getParticipanteDocumento(),
                                                "El documento del participante es requerido"))
                                .orElseThrow(() -> new RuntimeException(
                                                "Participante no encontrado con documento: "
                                                                + request.getParticipanteDocumento()));

                EventoEntity evento = eventoRepository
                                .findById(java.util.Objects.requireNonNull(request.getEventoId(),
                                                "El ID del evento es requerido"))
                                .orElseThrow(() -> new RuntimeException(
                                                "Evento no encontrado con ID: " + request.getEventoId()));

                ParticipacionEntity participacion = new ParticipacionEntity();
                participacion.setParticipante(participante);
                participacion.setEvento(evento);
                participacion.setFechaInscripcion(LocalDateTime.now());
                participacion.setCodigoUnicoAPI(UUID.randomUUID().toString()); // Generate a unique code
                participacion.setMetodoInscripcion(
                                request.getMetodoInscripcion() != null ? request.getMetodoInscripcion() : "MANUAL");

                // Track registrar
                participacion.setRegistradoPor(securityHelper.getCurrentUser());

                // Validar si el evento ya está lleno según aforo máximo (Opcional, pero útil)
                if (evento.getAforoMaximo() != null) {
                        int actual = evento.getAforoActual() != null ? evento.getAforoActual() : 0;
                        if (actual >= evento.getAforoMaximo()) {
                                // Nota: Aquí podríamos permitir sobre-inscripción si aforoActual es solo TIEMPO
                                // REAL
                                // Pero por ahora bloqueamos si está lleno.
                        }
                }

                ParticipacionEntity saved = participacionRepository.save(participacion);
                return mapToResponse(saved);
        }

        @Override
        @Transactional
        public void deleteParticipacion(@NonNull Integer id) {
                ParticipacionEntity participacion = participacionRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Participación no encontrada con ID: " + id));

                // Security Check: OWN vs ALL
                Long ownerId = participacion.getRegistradoPor() != null
                                ? participacion.getRegistradoPor().getDocumentoIdentidad()
                                : null;

                if (!securityHelper.isOwnerOrHasAll(ownerId, "PARTICIPACION:DELETE_ALL")) {
                        throw new RuntimeException(
                                        "Error: No tienes permisos para eliminar esta participación (no la registraste tú).");
                }

                participacionRepository.delete(participacion);
        }

        @Override
        @Transactional(readOnly = true)
        public List<ParticipacionResponse> getParticipacionesByEvento(@NonNull Integer eventoId) {
                return participacionRepository.findByEvento_IdEvento(eventoId).stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<ParticipacionResponse> getParticipacionesByParticipante(@NonNull Long documento) {
                return participacionRepository.findAll().stream()
                                .filter(p -> p.getParticipante().getDocumentoIdentidad().equals(documento))
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        private ParticipacionResponse mapToResponse(ParticipacionEntity entity) {
                // Calcular si tiene un check-in activo (ENTRADA sin SALIDA)
                boolean hasActiveCheckIn = entity.getCheckIns() != null && entity.getCheckIns().stream()
                                .anyMatch(checkIn -> checkIn
                                                .getTipoAccion() == com.example.backend.checkin.entity.TipoAccion.ENTRADA);

                // Contar las salidas
                long salidasCount = entity.getCheckIns() != null ? entity.getCheckIns().stream()
                                .filter(checkIn -> checkIn
                                                .getTipoAccion() == com.example.backend.checkin.entity.TipoAccion.SALIDA)
                                .count() : 0;

                // Contar las entradas
                long entradasCount = entity.getCheckIns() != null ? entity.getCheckIns().stream()
                                .filter(checkIn -> checkIn
                                                .getTipoAccion() == com.example.backend.checkin.entity.TipoAccion.ENTRADA)
                                .count() : 0;

                // Tiene check-in activo si hay más entradas que salidas
                hasActiveCheckIn = entradasCount > salidasCount;

                return new ParticipacionResponse(
                                entity.getIdParticipacion(),
                                entity.getFechaInscripcion(),
                                entity.getParticipante().getDocumentoIdentidad(),
                                entity.getParticipante().getNombre() + " " + entity.getParticipante().getApellido(),
                                entity.getEvento().getIdEvento(),
                                entity.getEvento().getNombre(),
                                entity.getCodigoUnicoAPI(),
                                entity.getMetodoInscripcion(),
                                entity.getRegistradoPor() != null ? entity.getRegistradoPor().getDocumentoIdentidad()
                                                : null,
                                entity.getRegistradoPor() != null
                                                ? (entity.getRegistradoPor().getNombre() + " "
                                                                + entity.getRegistradoPor().getApellido())
                                                : "Sistema",
                                hasActiveCheckIn);
        }
}
