package com.example.backend.auth;

import com.example.backend.auth.dto.JwtResponse;
import com.example.backend.auth.dto.LoginRequest;
import com.example.backend.auth.dto.SignupRequest;
import com.example.backend.auth.entity.ParticipanteEntity;
import com.example.backend.auth.repository.ParticipanteRepository;
import com.example.backend.config.jwt.JwtUtils;
import com.example.backend.rol.entity.AppRole;
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
        // Asignar roles
        java.util.Set<String> strRoles = signupRequest.getRoles();
        java.util.Set<RolEntity> roles = new java.util.HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            RolEntity userRole = rolRepository.findByNombreRol(AppRole.INVITADO.name())
                    .orElseThrow(() -> new RuntimeException("Error: Rol INVITADO no encontrado."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        RolEntity adminRole = rolRepository.findByNombreRol(AppRole.ADMIN.name())
                                .orElseThrow(() -> new RuntimeException("Error: Rol ADMIN no encontrado."));
                        roles.add(adminRole);
                        break;
                    case "organizador":
                        RolEntity modRole = rolRepository.findByNombreRol(AppRole.ORGANIZADOR.name())
                                .orElseThrow(() -> new RuntimeException("Error: Rol ORGANIZADOR no encontrado."));
                        roles.add(modRole);
                        break;
                    default:
                        // Try to find the role by name directly if it exists, matching dynamic roles
                        RolEntity userRole = rolRepository.findByNombreRol(role.toUpperCase())
                                .orElseGet(() -> rolRepository.findByNombreRol(AppRole.USER.name())
                                        .orElseThrow(() -> new RuntimeException("Error: Rol USER no encontrado.")));
                        roles.add(userRole);
                }
            });
        }
        participante.setRoles(roles);

        participanteRepository.save(participante);

        return "Usuario registrado exitosamente!";
    }
}
