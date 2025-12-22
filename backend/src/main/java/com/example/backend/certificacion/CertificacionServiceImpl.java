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
import com.example.backend.config.SecurityHelper;
import com.example.backend.auditoria.AuditoriaService;

@Service
public class CertificacionServiceImpl implements CertificacionService {

        @Autowired
        private CertificacionRepository certificacionRepository;

        @Autowired
        private ParticipacionRepository participacionRepository;

        @Autowired
        private SecurityHelper securityHelper;

        @Autowired
        private AuditoriaService auditoriaService;

        @Override
        @Transactional(readOnly = true)
        public List<CertificacionResponse> getAllCertificaciones() {
                List<CertificacionEntity> certificaciones;
                if (securityHelper.hasAuthority("CERTIFICADO:READ_ALL") || securityHelper.hasAuthority("ALL:ALL")) {
                        certificaciones = certificacionRepository.findAll();
                } else if (securityHelper.hasAuthority("CERTIFICADO:READ_OWN")) {
                        certificaciones = certificacionRepository.findAll().stream()
                                        .filter(c -> c.getEmitidoPor() != null &&
                                                        c.getEmitidoPor().getDocumentoIdentidad().equals(
                                                                        securityHelper.getCurrentUser()
                                                                                        .getDocumentoIdentidad()))
                                        .collect(Collectors.toList());
                } else {
                        return java.util.Collections.emptyList();
                }

                return certificaciones.stream()
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
                                .orElseThrow(() -> new RuntimeException(
                                                "Certificación no encontrada con código: " + codigo));
                return mapToResponse(certificacion);
        }

        @Override
        @Transactional
        public CertificacionResponse emitirCertificacion(CertificacionRequest request) {
                Integer participacionId = request.getParticipacionId();
                if (participacionId == null) {
                        throw new IllegalArgumentException(
                                        "El ID de la participación no puede ser nulo en la solicitud");
                }

                if (certificacionRepository.existsByParticipacion_IdParticipacion(participacionId)) {
                        throw new RuntimeException("Ya existe una certificación para esta participación.");
                }

                ParticipacionEntity participacion = participacionRepository.findById(participacionId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Participación no encontrada con ID: " + participacionId));

                if (participacion.getCheckIns() == null || participacion.getCheckIns().isEmpty()) {
                        throw new RuntimeException(
                                        "No se puede emitir certificación: El participante no tiene registro de asistencia (CheckIn).");
                }

                // Verificar que el participante haya hecho check-out (SALIDA)
                // Obtener el último check-in ordenado por fecha
                var ultimoCheckIn = participacion.getCheckIns().stream()
                                .max(java.util.Comparator.comparing(
                                                com.example.backend.checkin.entity.CheckInEntity::getFechaHoraCheckIn))
                                .orElse(null);

                if (ultimoCheckIn == null ||
                                ultimoCheckIn.getTipoAccion() != com.example.backend.checkin.entity.TipoAccion.SALIDA) {
                        throw new RuntimeException(
                                        "No se puede emitir certificación: El participante debe completar su asistencia registrando su salida (Check-Out) del evento antes de recibir el certificado.");
                }

                CertificacionEntity certificacion = new CertificacionEntity();
                certificacion.setParticipacion(participacion);
                certificacion.setFechaEmision(LocalDateTime.now());
                certificacion.setEmitido(true);
                certificacion.setCodigoUnicoAPI(UUID.randomUUID().toString());

                String fileName = "cert_" + participacion.getIdParticipacion() + "_" + System.currentTimeMillis()
                                + ".pdf";
                generatePdf(participacion, certificacion.getCodigoUnicoAPI(), fileName);

                // We assume a static resource handler maps /pdfs/** to the local pdfs directory
                certificacion.setRutaPDF("/pdfs/" + fileName);

                // Track issuer
                certificacion.setEmitidoPor(securityHelper.getCurrentUser());

                CertificacionEntity saved = certificacionRepository.save(certificacion);

                // Audit
                auditoriaService.registrarActividad("CERTIFICADO", "EMITIR",
                                "Emitido certificado ID: " + saved.getIdCertificacion() + " para participación: "
                                                + participacionId);

                return mapToResponse(saved);
        }

        private void generatePdf(ParticipacionEntity participacion, String codigo, String fileName) {
                try {
                        java.io.File dir = new java.io.File("pdfs");
                        if (!dir.exists())
                                dir.mkdirs();

                        // Configurar documento con márgenes mínimos para diseño full page
                        com.lowagie.text.Document document = new com.lowagie.text.Document(
                                        com.lowagie.text.PageSize.A4.rotate(), 0, 0, 0, 0);
                        com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(
                                        document, new java.io.FileOutputStream(new java.io.File(dir, fileName)));

                        document.open();
                        com.lowagie.text.pdf.PdfContentByte canvas = writer.getDirectContent();
                        float width = document.getPageSize().getWidth();
                        float height = document.getPageSize().getHeight();

                        // --- 1. FONDO PRINCIPAL PREMIUM (Blanco Hueso) ---
                        canvas.setColorFill(new java.awt.Color(252, 252, 255));
                        canvas.rectangle(0, 0, width, height);
                        canvas.fill();

                        // --- 2. BARRA LATERAL DECORATIVA (Gradiente simulado con rectangulos) ---
                        // Un diseño geométrico moderno a la izquierda
                        float sideWidth = 30;
                        canvas.setColorFill(new java.awt.Color(79, 70, 229)); // Indigo 600
                        canvas.rectangle(0, 0, sideWidth, height);
                        canvas.fill();

                        canvas.setColorFill(new java.awt.Color(67, 56, 202)); // Indigo 700
                        canvas.rectangle(sideWidth, 0, 10, height);
                        canvas.fill();

                        // --- 3. MARCO DORADO ELEGANTE ---
                        canvas.setLineWidth(2f);
                        canvas.setColorStroke(new java.awt.Color(212, 175, 55)); // Oro metálico
                        canvas.rectangle(60, 40, width - 100, height - 80);
                        canvas.stroke();

                        // --- 4. CABECERA ---
                        // Usamos ColumnText para posicionamiento absoluto preciso fuera de tablas
                        com.lowagie.text.pdf.ColumnText ct = new com.lowagie.text.pdf.ColumnText(canvas);

                        // "CERTIFICADO DE"
                        com.lowagie.text.Font titleFontSmall = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.HELVETICA, 16,
                                        new java.awt.Color(100, 116, 139)); // Slate 500
                        ct.setSimpleColumn(new com.lowagie.text.Phrase("CERTIFICADO DE", titleFontSmall),
                                        100, height - 80, width - 60, height - 120, 15,
                                        com.lowagie.text.Element.ALIGN_CENTER);
                        ct.go();

                        // "EXCELENCIA Y PARTICIPACIÓN"
                        com.lowagie.text.Font titleFontBig = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.HELVETICA_BOLD, 38,
                                        new java.awt.Color(30, 41, 59)); // Slate 800
                        ct.setSimpleColumn(new com.lowagie.text.Phrase("PARTICIPACIÓN", titleFontBig),
                                        100, height - 110, width - 60, height - 160, 40,
                                        com.lowagie.text.Element.ALIGN_CENTER);
                        ct.go();

                        // --- 5. CUERPO DEL TEXTO ---

                        // "OTORGADO A:"
                        com.lowagie.text.Font labelFont = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.HELVETICA_OBLIQUE, 14,
                                        new java.awt.Color(148, 163, 184)); // Slate 400
                        ct.setSimpleColumn(new com.lowagie.text.Phrase("Se hace constar que", labelFont),
                                        100, height - 190, width - 60, height - 210, 15,
                                        com.lowagie.text.Element.ALIGN_CENTER);
                        ct.go();

                        // NOMBRE DEL PARTICIPANTE
                        String nombreCompleto = participacion.getParticipante().getNombre() + " " +
                                        participacion.getParticipante().getApellido();
                        com.lowagie.text.Font nameFont = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.TIMES_BOLD, 42,
                                        new java.awt.Color(79, 70, 229)); // Indigo 600
                        ct.setSimpleColumn(new com.lowagie.text.Phrase(nombreCompleto.toUpperCase(), nameFont),
                                        80, height - 220, width - 60, height - 280, 45,
                                        com.lowagie.text.Element.ALIGN_CENTER);
                        ct.go();

                        // Línea separadora dorada bajo el nombre
                        canvas.setLineWidth(1f);
                        canvas.setColorStroke(new java.awt.Color(212, 175, 55));
                        float lineY = height - 285;
                        canvas.moveTo(width / 2 - 150, lineY);
                        canvas.lineTo(width / 2 + 150, lineY);
                        canvas.stroke();

                        // DESCRIPCIÓN DEL EVENTO
                        com.lowagie.text.Font bodyFont = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.HELVETICA, 14,
                                        new java.awt.Color(51, 65, 85)); // Slate 700

                        String eventoNombre = participacion.getEvento().getNombre();
                        String textoEvento = "Ha completado satisfactoriamente su asistencia al evento " + eventoNombre;

                        if (participacion.getEvento().getDuracionHoras() != null) {
                                textoEvento += " con una intensidad horaria de "
                                                + participacion.getEvento().getDuracionHoras() + " horas.";
                        } else {
                                textoEvento += ".";
                        }

                        com.lowagie.text.Paragraph pEvento = new com.lowagie.text.Paragraph(textoEvento, bodyFont);
                        pEvento.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);

                        ct.setSimpleColumn(pEvento,
                                        150, height - 310, width - 150, height - 380, 20,
                                        com.lowagie.text.Element.ALIGN_CENTER);
                        ct.go();

                        // --- 6. FECHA Y FIRMA ---
                        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(
                                        "dd 'de' MMMM 'de' yyyy", new java.util.Locale("es", "ES"));
                        String fecha = LocalDateTime.now().format(formatter);

                        com.lowagie.text.Font dateFont = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.HELVETICA_BOLD, 12,
                                        new java.awt.Color(30, 41, 59));

                        ct.setSimpleColumn(new com.lowagie.text.Phrase("Expedido el " + fecha, dateFont),
                                        100, 120, width - 60, 80, 15, com.lowagie.text.Element.ALIGN_CENTER);
                        ct.go();

                        // --- 7. FOOTER CON CÓDIGO VERIFICACIÓN Y BADGE ---
                        // Badge "VERIFICADO"
                        canvas.setColorFill(new java.awt.Color(220, 252, 231)); // Green 100
                        canvas.roundRectangle(width - 220, 55, 120, 30, 15);
                        canvas.fill();

                        com.lowagie.text.Font badgeFont = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.HELVETICA_BOLD, 10,
                                        new java.awt.Color(22, 101, 52)); // Green 800

                        com.lowagie.text.pdf.ColumnText.showTextAligned(canvas, com.lowagie.text.Element.ALIGN_CENTER,
                                        new com.lowagie.text.Phrase("100% VERIFICADO", badgeFont), width - 160, 64, 0);

                        // Código Hash
                        com.lowagie.text.Font hashFont = com.lowagie.text.FontFactory.getFont(
                                        com.lowagie.text.FontFactory.COURIER, 9,
                                        new java.awt.Color(148, 163, 184));

                        com.lowagie.text.pdf.ColumnText.showTextAligned(canvas, com.lowagie.text.Element.ALIGN_LEFT,
                                        new com.lowagie.text.Phrase("ID Único: " + codigo, hashFont), 70, 60, 0);
                        com.lowagie.text.pdf.ColumnText.showTextAligned(canvas, com.lowagie.text.Element.ALIGN_LEFT,
                                        new com.lowagie.text.Phrase("Sistema E-Pa-Ce", hashFont), 70, 50, 0);

                        // Logo
                        try {
                                java.net.URL logoUrl = getClass().getClassLoader().getResource("static/logo.png");
                                if (logoUrl != null) {
                                        com.lowagie.text.Image logo = com.lowagie.text.Image.getInstance(logoUrl);
                                        logo.scaleToFit(50, 50);
                                        logo.setAbsolutePosition(width / 2 - 25, 60);
                                        document.add(logo);
                                }
                        } catch (Exception e) {
                                // Ignore logo error
                        }

                        document.close();
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Error generando PDF Premium: " + e.getMessage());
                }
        }

        @Override
        @Transactional
        public void deleteCertificacion(@NonNull Integer id) {
                CertificacionEntity certificacion = certificacionRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Certificación no encontrada con ID: " + id));

                // Security Check: OWN vs ALL
                Long ownerId = certificacion.getEmitidoPor() != null
                                ? certificacion.getEmitidoPor().getDocumentoIdentidad()
                                : null;

                if (!securityHelper.isOwnerOrHasAll(ownerId, "CERTIFICADO:DELETE_ALL")) {
                        throw new RuntimeException(
                                        "Error: No tienes permisos para eliminar este certificado (no lo emitiste tú).");
                }

                // Audit
                auditoriaService.registrarActividad("CERTIFICADO", "DELETE", "Eliminado certificado ID: " + id);

                certificacionRepository.delete(certificacion);
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
                                entity.getParticipacion().getEvento().getNombre(),
                                entity.getParticipacion().getParticipante().getFotoPerfil());
        }
}
