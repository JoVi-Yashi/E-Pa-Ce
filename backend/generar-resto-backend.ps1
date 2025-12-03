# Script PowerShell para generar el RESTO del backend
# Ejecutar desde: backend/

Write-Host "🚀 Iniciando generación masiva del backend..." -ForegroundColor Green

# ==========================================
# 1. EXCEPTION HANDLING
# ==========================================
$globalExceptionHandler = @'
package com.example.backend.exceptions;

import com.example.backend.dtos.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            request.getDescription(false),
            HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
            
        ErrorResponse error = new ErrorResponse(
            "Error de validación",
            details,
            HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "Error interno del servidor",
            ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
'@

# ==========================================
# 2. DTOs FALTANTES
# ==========================================

# --- Evento DTOs ---
$eventoCreateDTO = @'
package com.example.backend.dtos.evento;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventoCreateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime fechaInicio;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDateTime fechaFin;
    
    private Integer aforoMaximo;
    
    @NotNull(message = "La modalidad es obligatoria")
    private Integer modalidadId;
    
    @NotNull(message = "El tipo de evento es obligatorio")
    private Integer tipoId;
}
'@

$eventoResponseDTO = @'
package com.example.backend.dtos.evento;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventoResponseDTO {
    private Integer idEvento;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Float duracionHoras;
    private Integer aforoMaximo;
    private String estado;
    private String modalidad;
    private String tipo;
}
'@

# --- Participante DTOs ---
$inscripcionRequest = @'
package com.example.backend.dtos.participante;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InscripcionRequest {
    @NotNull(message = "El ID del evento es obligatorio")
    private Integer eventoId;
    
    @NotNull(message = "El documento del participante es obligatorio")
    private Long documentoIdentidad;
}
'@

# --- CheckIn DTOs ---
$checkInRequest = @'
package com.example.backend.dtos.checkin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckInRequest {
    @NotBlank(message = "El código único es obligatorio")
    private String codigoUnico;
    
    private String metodo; // QR o MANUAL
}
'@

# ==========================================
# 3. SERVICIOS
# ==========================================

$eventoService = @'
package com.example.backend.services;

import com.example.backend.dtos.evento.EventoCreateDTO;
import com.example.backend.dtos.evento.EventoResponseDTO;
import com.example.backend.persistence.entity.EventoEntity;
import com.example.backend.persistence.entity.ModalidadEventoEntity;
import com.example.backend.persistence.entity.TipoEventoEntity;
import com.example.backend.repositories.EventoRepository;
import com.example.backend.repositories.ModalidadEventoRepository;
import com.example.backend.repositories.TipoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired private EventoRepository eventoRepository;
    @Autowired private ModalidadEventoRepository modalidadRepository;
    @Autowired private TipoEventoRepository tipoRepository;

    public EventoResponseDTO createEvento(EventoCreateDTO dto) {
        EventoEntity evento = new EventoEntity();
        evento.setNombre(dto.getNombre());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());
        evento.setAforoMaximo(dto.getAforoMaximo());
        evento.setEstado("ACTIVO");
        
        // Calcular duración
        long minutes = Duration.between(dto.getFechaInicio(), dto.getFechaFin()).toMinutes();
        evento.setDuracionHoras(minutes / 60.0f);
        
        ModalidadEventoEntity modalidad = modalidadRepository.findById(dto.getModalidadId())
            .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));
        evento.setModalidadEvento(modalidad);
        
        TipoEventoEntity tipo = tipoRepository.findById(dto.getTipoId())
            .orElseThrow(() -> new RuntimeException("Tipo no encontrado"));
        evento.setTipoEvento(tipo);
        
        EventoEntity saved = eventoRepository.save(evento);
        return mapToDTO(saved);
    }
    
    public List<EventoResponseDTO> getAllEventos() {
        return eventoRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    private EventoResponseDTO mapToDTO(EventoEntity entity) {
        EventoResponseDTO dto = new EventoResponseDTO();
        dto.setIdEvento(entity.getIdEvento());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setDuracionHoras(entity.getDuracionHoras());
        dto.setAforoMaximo(entity.getAforoMaximo());
        dto.setEstado(entity.getEstado());
        dto.setModalidad(entity.getModalidadEvento().getNombreModalidadEvento());
        dto.setTipo(entity.getTipoEvento().getNombreTipoEvento());
        return dto;
    }
}
'@

$participanteService = @'
package com.example.backend.services;

import com.example.backend.dtos.participante.InscripcionRequest;
import com.example.backend.persistence.entity.EventoEntity;
import com.example.backend.persistence.entity.ParticipacionEntity;
import com.example.backend.persistence.entity.ParticipanteEntity;
import com.example.backend.repositories.EventoRepository;
import com.example.backend.repositories.ParticipacionRepository;
import com.example.backend.repositories.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ParticipanteService {
    
    @Autowired private ParticipacionRepository participacionRepository;
    @Autowired private ParticipanteRepository participanteRepository;
    @Autowired private EventoRepository eventoRepository;
    
    public String inscribirParticipante(InscripcionRequest request) {
        // Validar existencia
        ParticipanteEntity participante = participanteRepository.findById(request.getDocumentoIdentidad())
            .orElseThrow(() -> new RuntimeException("Participante no encontrado"));
            
        EventoEntity evento = eventoRepository.findById(request.getEventoId())
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
            
        // Validar duplicado
        if(participacionRepository.existsByParticipanteAndEvento(request.getDocumentoIdentidad(), request.getEventoId())) {
            throw new RuntimeException("El participante ya está inscrito en este evento");
        }
        
        ParticipacionEntity participacion = new ParticipacionEntity();
        participacion.setParticipante(participante);
        participacion.setEvento(evento);
        participacion.setFechaInscripcion(LocalDateTime.now());
        participacion.setCodigoUnicoAPI(UUID.randomUUID().toString());
        
        participacionRepository.save(participacion);
        
        return "Inscripción exitosa. Código: " + participacion.getCodigoUnicoAPI();
    }
}
'@

# ==========================================
# 4. CONTROLADORES
# ==========================================

$eventoController = @'
package com.example.backend.controllers;

import com.example.backend.dtos.evento.EventoCreateDTO;
import com.example.backend.dtos.evento.EventoResponseDTO;
import com.example.backend.services.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/eventos")
public class EventoController {
    
    @Autowired private EventoService eventoService;
    
    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> getAllEventos() {
        return ResponseEntity.ok(eventoService.getAllEventos());
    }
    
    @PostMapping
    @PreAuthorize("hasRole(''ADMIN'') or hasRole(''OPERADOR'')")
    public ResponseEntity<EventoResponseDTO> createEvento(@Valid @RequestBody EventoCreateDTO dto) {
        return ResponseEntity.ok(eventoService.createEvento(dto));
    }
}
'@

$participanteController = @'
package com.example.backend.controllers;

import com.example.backend.dtos.common.MessageResponse;
import com.example.backend.dtos.participante.InscripcionRequest;
import com.example.backend.services.ParticipanteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {
    
    @Autowired private ParticipanteService participanteService;
    
    @PostMapping("/inscribir")
    public ResponseEntity<MessageResponse> inscribir(@Valid @RequestBody InscripcionRequest request) {
        String mensaje = participanteService.inscribirParticipante(request);
        return ResponseEntity.ok(new MessageResponse(mensaje));
    }
}
'@

# ==========================================
# CREAR DIRECTORIOS Y ARCHIVOS
# ==========================================

# Directorios
New-Item -ItemType Directory -Path "src/main/java/com/example/backend/exceptions" -Force | Out-Null

# Escribir archivos
$globalExceptionHandler | Out-File -FilePath "src/main/java/com/example/backend/exceptions/GlobalExceptionHandler.java" -Encoding UTF8
$eventoCreateDTO | Out-File -FilePath "src/main/java/com/example/backend/dtos/evento/EventoCreateDTO.java" -Encoding UTF8
$eventoResponseDTO | Out-File -FilePath "src/main/java/com/example/backend/dtos/evento/EventoResponseDTO.java" -Encoding UTF8
$inscripcionRequest | Out-File -FilePath "src/main/java/com/example/backend/dtos/participante/InscripcionRequest.java" -Encoding UTF8
$checkInRequest | Out-File -FilePath "src/main/java/com/example/backend/dtos/checkin/CheckInRequest.java" -Encoding UTF8

$eventoService | Out-File -FilePath "src/main/java/com/example/backend/services/EventoService.java" -Encoding UTF8
$participanteService | Out-File -FilePath "src/main/java/com/example/backend/services/ParticipanteService.java" -Encoding UTF8

$eventoController | Out-File -FilePath "src/main/java/com/example/backend/controllers/EventoController.java" -Encoding UTF8
$participanteController | Out-File -FilePath "src/main/java/com/example/backend/controllers/ParticipanteController.java" -Encoding UTF8

Write-Host "✅ Backend completado exitosamente!" -ForegroundColor Green
'@

$path = "c:\Users\johao\OneDrive\Documentos\JohaoCode\SENA\Fabrica\E-Pa-Ce\backend\generar-resto-backend.ps1"
$path | Out-File -FilePath $path -InputObject $scriptContent -Encoding UTF8
