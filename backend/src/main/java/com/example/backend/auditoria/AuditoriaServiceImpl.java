package com.example.backend.auditoria;

import com.example.backend.auditoria.dto.AuditoriaResponse;
import com.example.backend.auditoria.entity.AuditoriaEntity;
import com.example.backend.auditoria.repository.AuditoriaRepository;
import com.example.backend.auth.entity.ParticipanteEntity;
import com.example.backend.auth.repository.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Override
    @Transactional
    public void registrarAuditoria(String entidad, String accion, String descripcion, String ip, Long usuarioId) {
        AuditoriaEntity auditoria = new AuditoriaEntity();
        auditoria.setEntidadAfectada(entidad);
        auditoria.setAccion(accion);
        auditoria.setDescripcionCambio(descripcion);
        auditoria.setIpOrigen(ip);
        auditoria.setFechaHora(LocalDateTime.now());

        if (usuarioId != null) {
            ParticipanteEntity user = participanteRepository.findById(usuarioId).orElse(null);
            auditoria.setParticipante(user);
        }

        auditoriaRepository.save(auditoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaResponse> getAllLogs() {
        return auditoriaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaResponse> getLogsByParticipante(Long usuarioId) {
        return auditoriaRepository.findByParticipante_DocumentoIdentidad(usuarioId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AuditoriaResponse mapToResponse(AuditoriaEntity entity) {
        String userName = entity.getParticipante() != null
                ? entity.getParticipante().getNombre() + " " + entity.getParticipante().getApellido()
                : "ANONYMOUS";
        Long userId = entity.getParticipante() != null ? entity.getParticipante().getDocumentoIdentidad() : null;

        return new AuditoriaResponse(
                entity.getIdAuditoria(),
                entity.getFechaHora(),
                entity.getIpOrigen(),
                entity.getEntidadAfectada(),
                entity.getAccion(),
                entity.getDescripcionCambio(),
                userId,
                userName);
    }
}
