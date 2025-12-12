package com.example.backend.certificacion;

import com.example.backend.certificacion.dto.CertificacionRequest;
import com.example.backend.certificacion.dto.CertificacionResponse;
import java.util.List;

public interface CertificacionService {
    List<CertificacionResponse> getAllCertificaciones();

    CertificacionResponse getCertificacionById(Integer id);

    CertificacionResponse getCertificacionByCodigo(String codigo);

    CertificacionResponse emitirCertificacion(CertificacionRequest request);

    void deleteCertificacion(Integer id);

    List<CertificacionResponse> getCertificacionesByEvento(Integer eventoId);

    List<CertificacionResponse> getCertificacionesByParticipante(Long documento);
}
