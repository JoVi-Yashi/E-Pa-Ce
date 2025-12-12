package com.example.backend.certificacion;

import com.example.backend.certificacion.dto.CertificacionRequest;
import com.example.backend.certificacion.dto.CertificacionResponse;
import com.example.backend.certificacion.entity.CertificacionEntity;
import com.example.backend.certificacion.repository.CertificacionRepository;
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
public class CertificacionServiceImpl implements CertificacionService {

    @Autowired
    private CertificacionRepository certificacionRepository;

    @Autowired
    private ParticipacionRepository participacionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CertificacionResponse> getAllCertificaciones() {
        return certificacionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CertificacionResponse getCertificacionById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("The certification ID cannot be null");
        }
        CertificacionEntity certificacion = certificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificación no encontrada con ID: " + id));
        return mapToResponse(certificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public CertificacionResponse getCertificacionByCodigo(String codigo) {
        CertificacionEntity certificacion = certificacionRepository.findByCodigoUnicoAPI(codigo)
                .orElseThrow(() -> new RuntimeException("Certificación no encontrada con código: " + codigo));
        return mapToResponse(certificacion);
    }

    @Override
    @Transactional
    public CertificacionResponse emitirCertificacion(CertificacionRequest request) {
        Integer participacionId = request.getParticipacionId();
        if (participacionId == null) {
            throw new IllegalArgumentException("El ID de la participación no puede ser nulo en la solicitud");
        }

        if (certificacionRepository.existsByParticipacion_IdParticipacion(participacionId)) {
            throw new RuntimeException("Ya existe una certificación para esta participación.");
        }

        ParticipacionEntity participacion = participacionRepository.findById(participacionId)
                .orElseThrow(() -> new RuntimeException(
                        "Participación no encontrada con ID: " + participacionId));

        // Verificar si la participación tiene check-in (REGLA DE NEGOCIO: Solo si
        // asistió)
        // Esto depende de si CheckInEntity está presente en la ParticipacionEntity.
        // ParticipacionEntity tiene relación OneToOne con CheckInEntity.
        if (participacion.getCheckIn() == null) {
            throw new RuntimeException(
                    "No se puede emitir certificación: El participante no tiene registro de asistencia (CheckIn).");
        }

        CertificacionEntity certificacion = new CertificacionEntity();
        certificacion.setParticipacion(participacion);
        certificacion.setFechaEmision(LocalDateTime.now());
        certificacion.setEmitido(true);
        certificacion.setCodigoUnicoAPI(UUID.randomUUID().toString());
        // Ruta PDF simulada
        certificacion.setRutaPDF("/pdfs/cert_" + participacion.getIdParticipacion() + ".pdf");

        CertificacionEntity saved = certificacionRepository.save(certificacion);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCertificacion(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de certificación no puede ser nulo");
        }
        if (!certificacionRepository.existsById(id)) {
            throw new RuntimeException("Certificación no encontrada con ID: " + id);
        }
        certificacionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificacionResponse> getCertificacionesByEvento(Integer eventoId) {
        return certificacionRepository.findCertificadosEmitidosPorEvento(eventoId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificacionResponse> getCertificacionesByParticipante(Long documento) {
        return certificacionRepository.findCertificadosByParticipante(documento).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CertificacionResponse mapToResponse(CertificacionEntity entity) {
        return new CertificacionResponse(
                entity.getIdCertificacion(),
                entity.getCodigoUnicoAPI(),
                entity.getFechaEmision(),
                entity.getRutaPDF(),
                entity.getEmitido(),
                entity.getParticipacion().getIdParticipacion(),
                entity.getParticipacion().getParticipante().getNombre() + " "
                        + entity.getParticipacion().getParticipante().getApellido(),
                entity.getParticipacion().getEvento().getNombre());
    }
}
