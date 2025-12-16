package com.example.backend.auditoria;

import com.example.backend.auditoria.dto.AuditoriaResponse;

import org.springframework.lang.NonNull;
import java.util.List;

public interface AuditoriaService {
    void registrarAuditoria(String entidad, String accion, String descripcion, String ip, Long usuarioId);

    List<AuditoriaResponse> getAllLogs();

    List<AuditoriaResponse> getLogsByParticipante(@NonNull Long usuarioId);
}
