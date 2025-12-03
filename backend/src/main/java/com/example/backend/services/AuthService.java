package com.example.backend.services;

import com.example.backend.dtos.auth.JwtResponse;
import com.example.backend.dtos.auth.LoginRequest;
import com.example.backend.dtos.auth.SignupRequest;
import com.example.backend.dtos.common.MessageResponse;
import com.example.backend.persistence.entity.ParticipanteEntity;
import com.example.backend.persistence.entity.RolEntity;
import com.example.backend.repositories.ParticipanteRepository;
import com.example.backend.repositories.RolRepository;
import com.example.backend.config.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de autenticación.
 * Gestiona login, signup y generación de tokens JWT.
 */
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Autenticar usuario y generar token JWT.
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // Autenticar con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Obtener detalles del usuario
        ParticipanteEntity userDetails = (ParticipanteEntity) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(
                jwt,
                userDetails.getDocumentoIdentidad(),
                userDetails.getEmail(),
                userDetails.getNombre(),
                userDetails.getApellido(),
                roles);
    }

    /**
     * Registrar nuevo participante.
     */
    @Transactional
    public MessageResponse registerUser(SignupRequest signupRequest) {
        // Validar que el email no exista
        if (participanteRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Error: El email ya está en uso");
        }

        // Validar que el documento no exista
        if (participanteRepository.existsByDocumentoIdentidad(signupRequest.getDocumentoIdentidad())) {
            throw new RuntimeException("Error: El documento de identidad ya está registrado");
        }

        // Crear nuevo participante
        ParticipanteEntity participante = new ParticipanteEntity();
        participante.setDocumentoIdentidad(signupRequest.getDocumentoIdentidad());
        participante.setNombre(signupRequest.getNombre());
        participante.setApellido(signupRequest.getApellido());
        participante.setEmail(signupRequest.getEmail());
        participante.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        // Asignar rol (por defecto INVITADO si no se especifica)
        Integer rolId = signupRequest.getRolId() != null ? signupRequest.getRolId() : 4; // 4 = INVITADO
        RolEntity rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
        participante.setRol(rol);

        participanteRepository.save(participante);

        return new MessageResponse("Usuario registrado exitosamente");
    }
}
