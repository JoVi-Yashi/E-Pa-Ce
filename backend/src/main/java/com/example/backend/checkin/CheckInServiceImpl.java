package com.example.backend.checkin;

import com.example.backend.checkin.dto.CheckInRequest;
import com.example.backend.checkin.dto.CheckInResponse;
import com.example.backend.checkin.entity.CheckInEntity;
import com.example.backend.checkin.repository.CheckInRepository;
import com.example.backend.participacion.entity.ParticipacionEntity;
import com.example.backend.participacion.repository.ParticipacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckInServiceImpl implements CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private ParticipacionRepository participacionRepository;

    @Autowired
    private com.example.backend.auditoria.AuditoriaService auditoriaService;

    @Autowired
    private com.example.backend.evento.repository.EventoRepository eventoRepository;

    @Autowired
    private com.example.backend.config.SecurityHelper securityHelper;

    @Override
    @Transactional(readOnly = true)
    public List<CheckInResponse> getAllCheckIns() {
        List<CheckInEntity> checkIns;
        if (securityHelper.hasAuthority("CHECKIN:READ_ALL") || securityHelper.hasAuthority("ALL:ALL")) {
            checkIns = checkInRepository.findAll();
        } else if (securityHelper.hasAuthority("CHECKIN:READ_OWN")) {
            // Checkins registered by this operator (via related participation)
            checkIns = checkInRepository.findAll().stream()
                    .filter(c -> c.getParticipacion().getRegistradoPor() != null &&
                            c.getParticipacion().getRegistradoPor().getDocumentoIdentidad()
                                    .equals(securityHelper.getCurrentUser().getDocumentoIdentidad()))
                    .collect(Collectors.toList());
        } else {
            return java.util.Collections.emptyList();
        }

        return checkIns.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CheckInResponse getCheckInById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        CheckInEntity checkIn = checkInRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CheckIn no encontrado con ID: " + id));
        return mapToResponse(checkIn);
    }

    @Override
    @Transactional
    public CheckInResponse createCheckIn(CheckInRequest request) {
        Integer participacionId = request.getParticipacionId();

        ParticipacionEntity participacion;
        if (participacionId != null) {
            participacion = participacionRepository.findById(participacionId)
                    .orElseThrow(() -> new RuntimeException(
                            "Participación no encontrada con ID: " + participacionId));
        } else if (request.getCodigoUnicoAPI() != null) {
            throw new RuntimeException("Use el método realizarCheckInPorCodigo para check-in por código API.");
        } else {
            throw new RuntimeException("Debe proporcionar un ID de participación.");
        }

        // Determine Action Type
        com.example.backend.checkin.entity.TipoAccion tipo = com.example.backend.checkin.entity.TipoAccion.ENTRADA;
        if ("SALIDA".equalsIgnoreCase(request.getTipoAccion())) {
            tipo = com.example.backend.checkin.entity.TipoAccion.SALIDA;
        }

        // Validate the last state to prevent double entry or double exit
        var ultimoCheckIn = checkInRepository
                .findTopByParticipacion_IdParticipacionOrderByFechaHoraCheckInDesc(participacion.getIdParticipacion());

        String nombreParticipante = participacion.getParticipante().getNombre() + " "
                + participacion.getParticipante().getApellido();
        if (tipo == com.example.backend.checkin.entity.TipoAccion.ENTRADA) {
            if (ultimoCheckIn.isPresent()
                    && ultimoCheckIn.get().getTipoAccion() == com.example.backend.checkin.entity.TipoAccion.ENTRADA) {
                throw new RuntimeException(
                        "El participante " + nombreParticipante + " ya registró una ENTRADA activa.");
            }
        } else {
            if (ultimoCheckIn.isEmpty()
                    || ultimoCheckIn.get().getTipoAccion() == com.example.backend.checkin.entity.TipoAccion.SALIDA) {
                throw new RuntimeException("El participante " + nombreParticipante
                        + " no tiene una ENTRADA registrada para realizar SALIDA.");
            }
        }

        // Validate capacity only for ENTRADA
        var evento = participacion.getEvento();
        if (tipo == com.example.backend.checkin.entity.TipoAccion.ENTRADA) {
            if (evento.getAforoMaximo() != null && evento.getAforoActual() != null) {
                if (evento.getAforoActual() >= evento.getAforoMaximo()) {
                    throw new RuntimeException(
                            "El evento ha alcanzado su aforo máximo (" + evento.getAforoMaximo() + ")");
                }
            }
        }

        CheckInEntity checkIn = new CheckInEntity();
        checkIn.setParticipacion(participacion);
        checkIn.setFechaHoraCheckIn(LocalDateTime.now());
        checkIn.setMetodoCheckIn(request.getMetodoCheckIn() != null ? request.getMetodoCheckIn() : "MANUAL");
        checkIn.setIpCheckIn(request.getIpCheckIn());
        checkIn.setTipoAccion(tipo);

        // Link to participation to ensure it's included in the response calculation
        if (participacion.getCheckIns() == null) {
            participacion.setCheckIns(new java.util.ArrayList<>());
        }
        participacion.getCheckIns().add(checkIn);

        CheckInEntity saved = checkInRepository.save(checkIn);

        // Update capacity
        if (tipo == com.example.backend.checkin.entity.TipoAccion.ENTRADA) {
            evento.setAforoActual((evento.getAforoActual() != null ? evento.getAforoActual() : 0) + 1);
        } else {
            evento.setAforoActual(Math.max(0, (evento.getAforoActual() != null ? evento.getAforoActual() : 0) - 1));
        }
        eventoRepository.save(evento);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CheckInResponse realizarCheckInPorCodigo(String codigoUnicoAPI, String ipAddress) {
        ParticipacionEntity participacion = participacionRepository.findByCodigoUnicoAPI(codigoUnicoAPI)
                .orElseThrow(
                        () -> new RuntimeException("Participación no encontrada con código API: " + codigoUnicoAPI));

        // Validate the last state to prevent double entry or double exit
        var ultimoCheckIn = checkInRepository
                .findTopByParticipacion_IdParticipacionOrderByFechaHoraCheckInDesc(participacion.getIdParticipacion());

        if (ultimoCheckIn.isPresent()
                && ultimoCheckIn.get().getTipoAccion() == com.example.backend.checkin.entity.TipoAccion.ENTRADA) {
            throw new RuntimeException("Ya existe un CheckIn de ENTRADA activo para esta participación.");
        }

        // Validar aforo antes de permitir entrada
        var evento = participacion.getEvento();
        if (evento.getAforoMaximo() != null && evento.getAforoActual() != null) {
            if (evento.getAforoActual() >= evento.getAforoMaximo()) {
                throw new RuntimeException("El evento ha alcanzado su aforo máximo (" + evento.getAforoMaximo() + ")");
            }
        }

        CheckInEntity checkIn = new CheckInEntity();
        checkIn.setParticipacion(participacion);
        checkIn.setFechaHoraCheckIn(LocalDateTime.now());
        checkIn.setMetodoCheckIn("QR");
        checkIn.setIpCheckIn(ipAddress);
        checkIn.setTipoAccion(com.example.backend.checkin.entity.TipoAccion.ENTRADA);

        CheckInEntity saved = checkInRepository.save(checkIn);

        // Incrementar aforo actual del evento
        evento.setAforoActual((evento.getAforoActual() != null ? evento.getAforoActual() : 0) + 1);
        eventoRepository.save(evento);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CheckInResponse realizarCheckIn(Long participanteId, Integer eventoId, String ipAddress) {
        ParticipacionEntity participacion = participacionRepository
                .findByParticipante_DocumentoIdentidadAndEvento_IdEvento(participanteId, eventoId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada para este evento"));

        // Verificar el último estado para no duplicar aforo
        var ultimoCheckIn = checkInRepository
                .findTopByParticipacion_IdParticipacionOrderByFechaHoraCheckInDesc(participacion.getIdParticipacion());
        boolean yaEstaAdentro = ultimoCheckIn.isPresent()
                && ultimoCheckIn.get().getTipoAccion() == com.example.backend.checkin.entity.TipoAccion.ENTRADA;

        // Si no hay registros previos, el usuario está "AFUERA" (nunca ha entrado)
        // La inscripción NO incrementa el aforo actual, solo el check-in lo hace.
        if (ultimoCheckIn.isEmpty()) {
            yaEstaAdentro = false;
        }

        if (yaEstaAdentro) {
            // Ya está registrado como adentro, creamos el registro pero no incrementamos
            // aforo
            // (Podría ser útil para re-escanear sin afectar conteo)
        } else {
            // Estaba afuera (ultimo fue SALIDA), validar aforo y entrar
            validarAforo(participacion.getEvento());
            participacion.getEvento()
                    .setAforoActual((participacion.getEvento().getAforoActual() != null
                            ? participacion.getEvento().getAforoActual()
                            : 0) + 1);
            eventoRepository.save(participacion.getEvento());
        }

        CheckInEntity checkIn = new CheckInEntity();
        checkIn.setParticipacion(participacion);
        checkIn.setFechaHoraCheckIn(LocalDateTime.now());
        checkIn.setMetodoCheckIn("QR_SCANNER");
        checkIn.setIpCheckIn(ipAddress);
        checkIn.setTipoAccion(com.example.backend.checkin.entity.TipoAccion.ENTRADA);

        CheckInEntity saved = checkInRepository.save(checkIn);

        // Audit
        auditoriaService.registrarAuditoria("ASISTENCIA", "UPDATE",
                "Check-in realizado para ID " + participanteId + " en evento " + eventoId,
                ipAddress, participanteId, null);

        return mapToResponse(saved);
    }

    private void validarAforo(com.example.backend.evento.entity.EventoEntity evento) {
        if (evento.getAforoMaximo() != null) {
            int actual = evento.getAforoActual() != null ? evento.getAforoActual() : 0;
            if (actual >= evento.getAforoMaximo()) {
                throw new RuntimeException("El evento ha alcanzado su aforo máximo (" + evento.getAforoMaximo() + ")");
            }
        }
    }

    @Override
    @Transactional
    public CheckInResponse realizarCheckOut(Long participanteId, Integer eventoId, String ipAddress) {
        ParticipacionEntity participacion = participacionRepository
                .findByParticipante_DocumentoIdentidadAndEvento_IdEvento(participanteId, eventoId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada para este evento"));

        var evento = participacion.getEvento();

        // Verificar el último estado
        var ultimoCheckIn = checkInRepository
                .findTopByParticipacion_IdParticipacionOrderByFechaHoraCheckInDesc(participacion.getIdParticipacion());
        boolean yaEstaAfuera = ultimoCheckIn.isPresent()
                && ultimoCheckIn.get().getTipoAccion() == com.example.backend.checkin.entity.TipoAccion.SALIDA;

        if (!yaEstaAfuera) {
            // Estaba adentro, decrementar aforo
            int aforo = (evento.getAforoActual() != null ? evento.getAforoActual() : 0);
            evento.setAforoActual(Math.max(0, aforo - 1));
            eventoRepository.save(evento);
        }

        CheckInEntity checkIn = new CheckInEntity();
        checkIn.setParticipacion(participacion);
        checkIn.setFechaHoraCheckIn(LocalDateTime.now());
        checkIn.setMetodoCheckIn("QR_SCANNER");
        checkIn.setIpCheckIn(ipAddress);
        checkIn.setTipoAccion(com.example.backend.checkin.entity.TipoAccion.SALIDA);

        CheckInEntity saved = checkInRepository.save(checkIn);

        // Audit
        auditoriaService.registrarAuditoria("ASISTENCIA", "UPDATE",
                "Check-out (salida) realizado para ID " + participanteId + " en evento " + eventoId, ipAddress,
                participanteId, null);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCheckIn(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        if (!checkInRepository.existsById(id)) {
            throw new RuntimeException("CheckIn no encontrado con ID: " + id);
        }
        checkInRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckInResponse> getCheckInsByEvento(Integer eventoId) {
        return checkInRepository.findCheckInsByEvento(eventoId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckInResponse> getActiveCheckInsByEvento(Integer eventoId) {
        return checkInRepository.findActiveCheckInsByEvento(eventoId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CheckInResponse mapToResponse(CheckInEntity entity) {
        return new CheckInResponse(
                entity.getIdCheckIn(),
                entity.getFechaHoraCheckIn(),
                entity.getMetodoCheckIn(),
                entity.getIpCheckIn(),
                entity.getParticipacion().getIdParticipacion(),
                entity.getParticipacion().getParticipante().getNombre() + " "
                        + entity.getParticipacion().getParticipante().getApellido(),
                entity.getParticipacion().getEvento().getNombre(),
                entity.getParticipacion().getEvento().getIdEvento(),
                entity.getParticipacion().getParticipante().getDocumentoIdentidad(),
                entity.getTipoAccion() != null ? entity.getTipoAccion().toString() : "ENTRADA",
                entity.getParticipacion().getParticipante().getFotoPerfil());
    }
}
