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
import org.springframework.lang.NonNull;

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
    public CertificacionResponse getCertificacionById(@NonNull Integer id) {
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

        if (participacion.getCheckIn() == null) {
            throw new RuntimeException(
                    "No se puede emitir certificación: El participante no tiene registro de asistencia (CheckIn).");
        }

        CertificacionEntity certificacion = new CertificacionEntity();
        certificacion.setParticipacion(participacion);
        certificacion.setFechaEmision(LocalDateTime.now());
        certificacion.setEmitido(true);
        certificacion.setCodigoUnicoAPI(UUID.randomUUID().toString());

        String fileName = "cert_" + participacion.getIdParticipacion() + "_" + System.currentTimeMillis() + ".pdf";
        generatePdf(participacion, certificacion.getCodigoUnicoAPI(), fileName);

        // We assume a static resource handler maps /pdfs/** to the local pdfs directory
        certificacion.setRutaPDF("/pdfs/" + fileName);

        CertificacionEntity saved = certificacionRepository.save(certificacion);
        return mapToResponse(saved);
    }

    private void generatePdf(ParticipacionEntity participacion, String codigo, String fileName) {
        try {
            java.io.File dir = new java.io.File("pdfs");
            if (!dir.exists())
                dir.mkdirs();

            com.lowagie.text.Document document = new com.lowagie.text.Document();
            com.lowagie.text.pdf.PdfWriter.getInstance(document,
                    new java.io.FileOutputStream(new java.io.File(dir, fileName)));
            document.open();

            com.lowagie.text.Font titleFont = com.lowagie.text.FontFactory
                    .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 24);
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("CERTIFICADO DE ASISTENCIA", titleFont);
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(title);

            document.add(new com.lowagie.text.Paragraph("\n\n"));

            com.lowagie.text.Paragraph body = new com.lowagie.text.Paragraph("Certificamos que\n\n" +
                    participacion.getParticipante().getNombre() + " " + participacion.getParticipante().getApellido() +
                    "\n\nHa asistido satisfactoriamente al evento:\n\n" +
                    participacion.getEvento().getNombre() +
                    "\n\nFecha de emisión: " + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE));
            body.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(body);

            document.add(new com.lowagie.text.Paragraph("\n\n"));
            com.lowagie.text.Paragraph footer = new com.lowagie.text.Paragraph("Código de verificación: " + codigo);
            footer.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteCertificacion(@NonNull Integer id) {
        if (!certificacionRepository.existsById(id)) {
            throw new RuntimeException("Certificación no encontrada con ID: " + id);
        }
        certificacionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificacionResponse> getCertificacionesByEvento(@NonNull Integer eventoId) {
        return certificacionRepository.findCertificadosEmitidosPorEvento(eventoId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificacionResponse> getCertificacionesByParticipante(@NonNull Long documento) {
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
