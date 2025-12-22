package com.example.backend.auditoria;

import com.example.backend.auditoria.dto.AuditoriaResponse;
import com.example.backend.auditoria.entity.AuditoriaEntity;
import com.example.backend.auditoria.repository.AuditoriaRepository;
import com.example.backend.auth.entity.ParticipanteEntity;
import com.example.backend.auth.repository.ParticipanteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;
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

    @Autowired
    private HttpServletRequest request;

    @Override
    @Transactional
    public void registrarActividad(String entidad, String accion, String descripcion) {
        // Obtener rol activo del header (si viene del frontend)
        String activeRole = null;
        if (request != null) {
            activeRole = request.getHeader("X-Active-Role");
        }
        registrarActividad(entidad, accion, descripcion, activeRole);
    }

    @Override
    @Transactional
    public void registrarActividad(String entidad, String accion, String descripcion, String rol) {
        String ip = "UNKNOWN";
        if (request != null) {
            ip = request.getRemoteAddr();
            // Si hay un proxy o load balancer
            String xHeader = request.getHeader("X-Forwarded-For");
            if (xHeader != null && !xHeader.isEmpty()) {
                ip = xHeader.split(",")[0];
            }
        }

        Long usuarioId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof ParticipanteEntity) {
            usuarioId = ((ParticipanteEntity) auth.getPrincipal()).getDocumentoIdentidad();
        }

        registrarAuditoria(entidad, accion, descripcion, ip, usuarioId, rol);
    }

    @Override
    @Transactional
    public void registrarAuditoria(String entidad, String accion, String descripcion, String ip, Long usuarioId,
            String rol) {
        AuditoriaEntity auditoria = new AuditoriaEntity();
        auditoria.setEntidadAfectada(entidad);
        auditoria.setAccion(accion);
        auditoria.setDescripcionCambio(descripcion);
        auditoria.setIpOrigen(ip);
        auditoria.setFechaHora(LocalDateTime.now());
        auditoria.setRolUsuario(rol);

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
    public List<AuditoriaResponse> getLogsByParticipante(@NonNull Long usuarioId) {
        return auditoriaRepository.findByParticipante_DocumentoIdentidad(usuarioId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AuditoriaResponse mapToResponse(AuditoriaEntity entity) {
        String userName = entity.getParticipante() != null
                ? entity.getParticipante().getNombre() + " " + entity.getParticipante().getApellido()
                : "ANONYMOUS";
        Long userId = entity.getParticipante() != null ? entity.getParticipante().getDocumentoIdentidad() : null;
        String userFoto = entity.getParticipante() != null ? entity.getParticipante().getFotoPerfil() : null;

        return new AuditoriaResponse(
                entity.getIdAuditoria(),
                entity.getFechaHora(),
                entity.getIpOrigen(),
                entity.getEntidadAfectada(),
                entity.getAccion(),
                entity.getDescripcionCambio(),
                userId,
                userName,
                userFoto,
                entity.getRolUsuario());
    }

    @Override
    @Transactional
    public void desvincularUsuario(Long usuarioId) {
        List<AuditoriaEntity> logs = auditoriaRepository.findByParticipante_DocumentoIdentidad(usuarioId);
        for (AuditoriaEntity log : logs) {
            log.setParticipante(null);
        }
        auditoriaRepository.saveAll(logs);
    }
}
