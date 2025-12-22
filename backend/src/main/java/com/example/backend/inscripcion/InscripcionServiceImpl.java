package com.example.backend.inscripcion;

import com.example.backend.auditoria.AuditoriaService;
import com.example.backend.auth.entity.ParticipanteEntity;
import com.example.backend.auth.repository.ParticipanteRepository;
import com.example.backend.evento.entity.EventoEntity;
import com.example.backend.evento.repository.EventoRepository;
import com.example.backend.inscripcion.entity.InscripcionEntity;
import com.example.backend.inscripcion.repository.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscripcionServiceImpl implements InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Override
    @Transactional
    public InscripcionEntity inscribir(Long participanteId, Integer eventoId) {
        if (inscripcionRepository.findByParticipante_DocumentoIdentidadAndEvento_IdEvento(participanteId, eventoId)
                .isPresent()) {
            throw new RuntimeException("El participante ya está inscrito en este evento");
        }

        ParticipanteEntity participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new RuntimeException("Participante no encontrado"));
        EventoEntity evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        InscripcionEntity inscripcion = new InscripcionEntity();
        inscripcion.setParticipante(participante);
        inscripcion.setEvento(evento);

        InscripcionEntity saved = inscripcionRepository.save(inscripcion);

        // Audit
        auditoriaService.registrarAuditoria("INSCRIPCION", "CREATE",
                "Participante " + participanteId + " inscrito en evento " + eventoId,
                "SYSTEM", participanteId, null);

        return saved;
    }

    @Override
    @Transactional
    public InscripcionEntity checkIn(Long participanteId, Integer eventoId) {
        InscripcionEntity inscripcion = inscripcionRepository
                .findByParticipante_DocumentoIdentidadAndEvento_IdEvento(participanteId, eventoId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada para este evento"));

        if (inscripcion.isCheckedIn()) {
            throw new RuntimeException("El participante ya ha realizado check-in");
        }

        inscripcion.setCheckedIn(true);
        inscripcion.setFechaCheckIn(LocalDateTime.now());

        InscripcionEntity saved = inscripcionRepository.save(inscripcion);

        // Audit
        auditoriaService.registrarAuditoria("ASISTENCIA", "UPDATE",
                "Check-in realizado para participante " + participanteId + " en evento " + eventoId,
                "QR_SCANNER", participanteId, null);

        return saved;
    }

    @Override
    public List<InscripcionEntity> getInscripcionesPorParticipante(Long participanteId) {
        return inscripcionRepository.findByParticipante_DocumentoIdentidad(participanteId);
    }

    @Override
    public List<InscripcionEntity> getInscripcionesPorEvento(Integer eventoId) {
        return inscripcionRepository.findByEvento_IdEvento(eventoId);
    }

    @Override
    public List<InscripcionEntity> getAllInscripciones() {
        return inscripcionRepository.findAll();
    }
}
