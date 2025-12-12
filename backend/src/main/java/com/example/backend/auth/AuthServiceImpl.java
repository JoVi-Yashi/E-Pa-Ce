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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        ParticipanteEntity userDetails = (ParticipanteEntity) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getDocumentoIdentidad(),
                userDetails.getEmail(),
                userDetails.getNombre(),
                userDetails.getApellido(),
                roles);
    }

    @Override
    public String registerUser(SignupRequest signupRequest) {
        if (participanteRepository.existsByEmail(signupRequest.getEmail())) {
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
        participante.setEmail(signupRequest.getEmail());
        participante.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        // Asignar rol
        Integer rolId = signupRequest.getRolId();
        if (rolId == null) {
            // Default role: INVITADO (Asumiendo ID 3 por ejemplo, o buscando por nombre)
            // Mejor buscar por nombre si es posible, o ID 1 si es el default
            RolEntity userRole = rolRepository.findByNombreRol("INVITADO")
                    .orElseThrow(() -> new RuntimeException("Error: Rol INVITADO no encontrado."));
            participante.setRol(userRole);
        } else {
            RolEntity rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            participante.setRol(rol);
        }

        participanteRepository.save(participante);

        return "Usuario registrado exitosamente!";
    }
}
