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

    @Override
    @Transactional(readOnly = true)
    public List<CheckInResponse> getAllCheckIns() {
        return checkInRepository.findAll().stream()
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
        if (participacionId != null
                && checkInRepository.existsByParticipacion_IdParticipacion(participacionId)) {
            throw new RuntimeException("Ya existe un CheckIn para esta participación.");
        }

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

        CheckInEntity checkIn = new CheckInEntity();
        checkIn.setParticipacion(participacion);
        checkIn.setFechaHoraCheckIn(LocalDateTime.now());
        checkIn.setMetodoCheckIn(request.getMetodoCheckIn() != null ? request.getMetodoCheckIn() : "MANUAL");
        checkIn.setIpCheckIn(request.getIpCheckIn());

        CheckInEntity saved = checkInRepository.save(checkIn);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CheckInResponse realizarCheckInPorCodigo(String codigoUnicoAPI, String ipAddress) {
        ParticipacionEntity participacion = participacionRepository.findByCodigoUnicoAPI(codigoUnicoAPI)
                .orElseThrow(
                        () -> new RuntimeException("Participación no encontrada con código API: " + codigoUnicoAPI));

        if (checkInRepository.existsByParticipacion_IdParticipacion(participacion.getIdParticipacion())) {
            throw new RuntimeException("Ya existe un CheckIn para esta participación.");
        }

        CheckInEntity checkIn = new CheckInEntity();
        checkIn.setParticipacion(participacion);
        checkIn.setFechaHoraCheckIn(LocalDateTime.now());
        checkIn.setMetodoCheckIn("QR");
        checkIn.setIpCheckIn(ipAddress);

        CheckInEntity saved = checkInRepository.save(checkIn);
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

    private CheckInResponse mapToResponse(CheckInEntity entity) {
        return new CheckInResponse(
                entity.getIdCheckIn(),
                entity.getFechaHoraCheckIn(),
                entity.getMetodoCheckIn(),
                entity.getIpCheckIn(),
                entity.getParticipacion().getIdParticipacion(),
                entity.getParticipacion().getParticipante().getNombre() + " "
                        + entity.getParticipacion().getParticipante().getApellido(),
                entity.getParticipacion().getEvento().getNombre());
    }
}
