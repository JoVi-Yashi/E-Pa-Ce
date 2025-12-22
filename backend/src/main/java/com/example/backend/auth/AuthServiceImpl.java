package com.example.backend.auth;

import com.example.backend.auth.dto.JwtResponse;
import com.example.backend.auth.dto.LoginRequest;
import com.example.backend.auth.dto.SignupRequest;
import com.example.backend.auth.entity.ParticipanteEntity;
import com.example.backend.auth.repository.ParticipanteRepository;
import com.example.backend.config.jwt.JwtUtils;
import com.example.backend.rol.entity.RolEntity;
import com.example.backend.rol.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.example.backend.auth.entity.PasswordResetToken;
import com.example.backend.auth.repository.PasswordResetTokenRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.PersistenceContext;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    @Autowired
    private com.example.backend.auditoria.AuditoriaService auditoriaService;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    private HttpServletRequest request;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail() != null ? loginRequest.getEmail().toLowerCase() : null;
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        ParticipanteEntity userDetails = (ParticipanteEntity) authentication.getPrincipal();

        // ONLY roles (ROLE_ADMIN, etc), NOT individual permissions
        List<String> roles = userDetails.getRoles().stream()
                .map(rol -> "ROLE_" + rol.getNombreRol())
                .collect(Collectors.toList());

        // Create a mapping of role to its specific permissions for frontend use
        Map<String, List<String>> rolePermissions = new HashMap<>();
        for (RolEntity rol : userDetails.getRoles()) {
            String roleName = rol.getNombreRol();
            rolePermissions.put(roleName, new java.util.ArrayList<>(rol.getPermisos()));
        }

        // Determine default active role by hierarchy
        String activeRole = userDetails.getHighestPriorityRole();

        // Audit login
        auditoriaService.registrarActividad("AUTH", "LOGIN",
                "Usuario " + userDetails.getEmail() + " inició sesión con rol " + activeRole, activeRole);

        System.out.println("DEBUG: LOGIN - Roles: " + roles);
        System.out.println("DEBUG: LOGIN - Permissions Map: " + rolePermissions);

        return new JwtResponse(jwt,
                userDetails.getDocumentoIdentidad(),
                userDetails.getEmail(),
                userDetails.getNombre(),
                userDetails.getApellido(),
                roles,
                rolePermissions,
                activeRole,
                userDetails.getFotoPerfil());
    }

    @Override
    @Transactional
    public String registerUser(SignupRequest signupRequest) {
        String email = signupRequest.getEmail() != null ? signupRequest.getEmail().toLowerCase() : null;
        if (participanteRepository.existsByEmail(email)) {
            throw new RuntimeException("Error: El email ya está en uso!");
        }

        if (participanteRepository.existsByDocumentoIdentidad(signupRequest.getDocumentoIdentidad())) {
            throw new RuntimeException("Error: El documento de identidad ya está registrado!");
        }

        // Crear nuevo participante
        ParticipanteEntity participante = new ParticipanteEntity();
        participante.setDocumentoIdentidad(signupRequest.getDocumentoIdentidad());
        participante.setNombre(signupRequest.getNombre());
        participante.setApellido(signupRequest.getApellido());
        participante.setEmail(email);
        participante.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        // Asignar rol
        // Asignar roles
        java.util.Set<String> strRoles = signupRequest.getRoles();
        java.util.Set<RolEntity> roles = new java.util.HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Default role if none provided
            RolEntity defaultRole = rolRepository.findByNombreRol("MONITOR")
                    .orElseThrow(() -> new RuntimeException("Error: No se encontró el rol MONITOR por defecto."));
            roles.add(defaultRole);
        } else {
            strRoles.forEach(roleName -> {
                // Try to find role by exact name first
                java.util.Optional<RolEntity> roleOpt = rolRepository.findByNombreRol(roleName);

                // If not found, try uppercase
                if (roleOpt.isEmpty()) {
                    roleOpt = rolRepository.findByNombreRol(roleName.toUpperCase());
                }

                // If still not found, try without ROLE_ prefix if present
                if (roleOpt.isEmpty() && roleName.startsWith("ROLE_")) {
                    roleOpt = rolRepository.findByNombreRol(roleName.substring(5));
                }

                if (roleOpt.isPresent()) {
                    roles.add(roleOpt.get());
                } else {
                    throw new RuntimeException("Error: Rol '" + roleName + "' no encontrado en el sistema.");
                }
            });
        }
        participante.setRoles(roles);

        ParticipanteEntity saved = participanteRepository.save(participante);

        // Audit signup
        auditoriaService.registrarAuditoria("PARTICIPANTE", "CREATE",
                "Nuevo registro: " + saved.getNombre() + " (" + saved.getEmail() + ")",
                "REGISTRATION_FORM", saved.getDocumentoIdentidad(), null);

        return "Usuario registrado exitosamente!";
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        String cleanEmail = email != null ? email.toLowerCase() : null;
        ParticipanteEntity participante = participanteRepository.findByEmail(cleanEmail)
                .orElseThrow(
                        () -> new RuntimeException("Error: No se encontró un usuario con el correo proporcionado."));

        // Eliminar token previo si existe
        passwordResetTokenRepository.deleteByParticipante(participante);

        // Generar código de 6 dígitos al azar
        String code = String.format("%06d", new java.util.Random().nextInt(1000000));
        PasswordResetToken resetToken = new PasswordResetToken(code, participante);
        passwordResetTokenRepository.save(resetToken);

        // Enviar correo
        sendResetEmail(participante.getEmail(), code, participante.getNombre());

        auditoriaService.registrarActividad("AUTH", "FORGOT_PASSWORD", "Solicitud de recuperación para: " + email);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String email, String newPassword) {
        // Find token by code AND verify it belongs to the correct email for extra
        // security
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Error: El código de verificación no es válido."));

        String cleanEmail = email != null ? email.toLowerCase() : null;
        if (!resetToken.getParticipante().getEmail().equals(cleanEmail)) {
            throw new RuntimeException("Error: El código no corresponde a esta cuenta de correo.");
        }

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Error: El código ha expirado. Por favor solicite uno nuevo.");
        }

        ParticipanteEntity participante = resetToken.getParticipante();
        participante.setPassword(passwordEncoder.encode(newPassword));
        participanteRepository.save(participante);

        // Limpiar tokens
        passwordResetTokenRepository.deleteByParticipante(participante);

        auditoriaService.registrarActividad("AUTH", "RESET_PASSWORD",
                "Contraseña restablecida para: " + participante.getEmail());
    }

    @Autowired
    private org.springframework.core.env.Environment env;

    private void sendResetEmail(String userEmail, String code, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String fromEmail = env.getProperty("spring.mail.username");
            if (fromEmail != null && fromEmail.contains("${")) {
                fromEmail = "mail.epace@gmail.com";
            }

            helper.setFrom(fromEmail);
            helper.setTo(userEmail);
            helper.setSubject("Código de Verificación - EPaCe");

            String resetUrl = "http://localhost:5173/reset-password?email=" + userEmail;

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f4f7f6; margin: 0; padding: 0;'>" +
                    "  <div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1);'>"
                    +
                    "    <div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;'>"
                    +
                    "      <h1 style='color: #ffffff; margin: 0; font-size: 24px;'>Recuperación de Contraseña</h1>" +
                    "    </div>" +
                    "    <div style='padding: 30px; color: #4a5568; line-height: 1.6;'>" +
                    "      <p style='font-size: 18px; margin-bottom: 20px;'>Hola <strong>" + userName + "</strong>,</p>"
                    +
                    "      <p>Has solicitado restablecer tu contraseña en <strong>EPaCe</strong>. Usa el siguiente código de verificación para continuar:</p>"
                    +
                    "      " +
                    "      <div style='text-align: center; margin: 40px 0;'>" +
                    "        <div style='display: inline-block; background: #f7fafc; padding: 20px 40px; border-radius: 12px; border: 2px dashed #667eea;'>"
                    +
                    "           <span style='font-size: 32px; font-weight: 800; letter-spacing: 12px; color: #2d3748;'>"
                    + code + "</span>" +
                    "        </div>" +
                    "      </div>" +
                    "      " +
                    "      <p style='text-align: center;'>" +
                    "        <a href='" + resetUrl
                    + "' style='color: #667eea; font-weight: bold;'>Haga clic aquí para ingresar el código</a>" +
                    "      </p>" +
                    "      " +
                    "      <hr style='border: 0; border-top: 1px solid #e2e8f0; margin: 30px 0;'>" +
                    "      <p style='font-size: 13px; color: #a0aec0;'>Por su seguridad, no comparta este código con nadie. El código expirará en 2 horas.</p>"
                    +
                    "    </div>" +
                    "    <div style='background-color: #f8fafc; padding: 20px; text-align: center; color: #718096; font-size: 12px;'>"
                    +
                    "      &copy; 2025 EPaCe - Gestión de Seguridad" +
                    "    </div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el correo de verificación.");
        }
    }

    @Override
    @Transactional
    public JwtResponse refreshToken() {
        try {
            entityManager.clear(); // Limpiar cache de persistencia para forzar lectura fresca de la BD
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("Error: No se encontró una sesión válida en el servidor.");
            }

            String userEmail = authentication.getName();
            ParticipanteEntity userDetails = participanteRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));

            String jwt = jwtUtils.generateJwtToken(authentication);

            List<String> roles = userDetails.getRoles().stream()
                    .map(rol -> "ROLE_" + rol.getNombreRol())
                    .collect(Collectors.toList());

            Map<String, List<String>> rolePermissions = new HashMap<>();
            for (RolEntity rol : userDetails.getRoles()) {
                // Re-fetch from DB to avoid cache staleness
                RolEntity freshRol = rolRepository.findById(rol.getIdRol()).orElse(rol);
                rolePermissions.put(freshRol.getNombreRol(), new java.util.ArrayList<>(freshRol.getPermisos()));
            }

            String activeRole = request != null ? request.getHeader("X-Active-Role") : null;
            if (activeRole == null)
                activeRole = userDetails.getHighestPriorityRole();

            auditoriaService.registrarActividad("AUTH", "SESSION_EXTEND",
                    "Sesión extendida para " + userEmail, activeRole);

            return new JwtResponse(jwt,
                    userDetails.getDocumentoIdentidad(),
                    userDetails.getEmail(),
                    userDetails.getNombre(),
                    userDetails.getApellido(),
                    roles,
                    rolePermissions,
                    activeRole,
                    userDetails.getFotoPerfil());
        } catch (Exception e) {
            System.err.println("--- ERROR CRÍTICO EN REFRESH TOKEN ---");
            e.printStackTrace();
            throw new RuntimeException("Error al refrescar la sesión: " + e.getMessage());
        }
    }
}
