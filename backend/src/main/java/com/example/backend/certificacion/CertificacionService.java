package com.example.backend.certificacion;

import com.example.backend.certificacion.dto.CertificacionRequest;
import com.example.backend.certificacion.dto.CertificacionResponse;
import org.springframework.lang.NonNull;
import java.util.List;

public interface CertificacionService {
    List<CertificacionResponse> getAllCertificaciones();

    CertificacionResponse getCertificacionById(@NonNull Integer id);

    CertificacionResponse getCertificacionByCodigo(String codigo);

    CertificacionResponse emitirCertificacion(CertificacionRequest request);

    void deleteCertificacion(@NonNull Integer id);

    List<CertificacionResponse> getCertificacionesByEvento(@NonNull Integer eventoId);

    List<CertificacionResponse> getCertificacionesByParticipante(@NonNull Long documento);
}
