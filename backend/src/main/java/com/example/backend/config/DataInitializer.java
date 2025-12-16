package com.example.backend.config;

import com.example.backend.auth.entity.ParticipanteEntity;
import com.example.backend.auth.repository.ParticipanteRepository;
import com.example.backend.rol.entity.AppRole;
import com.example.backend.rol.entity.RolEntity;
import com.example.backend.rol.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final ParticipanteRepository participanteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository,
            ParticipanteRepository participanteRepository,
            PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.participanteRepository = participanteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Create roles if they don't exist
        // 1. Create roles if they don't exist
        for (AppRole appRole : AppRole.values()) {
            if (!rolRepository.existsByNombreRol(appRole.name())) {
                RolEntity rol = new RolEntity(appRole.name());
                // Add default permissions if ADMIN
                if (appRole == AppRole.ADMIN) {
                    rol.getPermisos().add("ALL:ALL");
                }
                rolRepository.save(rol);
                System.out.println("Rol creado: " + appRole.name());
            }
        }

        // 2. Create test users if they don't exist
        createUserIfNotExists(1000000001L, "Admin", "Sistema", "admin@epace.com", "admin123", AppRole.ADMIN);
        createUserIfNotExists(1000000002L, "User", "Prueba", "user@epace.com", "test123", AppRole.USER);
        createUserIfNotExists(1000000003L, "Organizador", "Prueba", "organizador@epace.com", "test123",
                AppRole.ORGANIZADOR);
        createUserIfNotExists(1000000004L, "Invitado", "Prueba", "invitado@epace.com", "test123", AppRole.INVITADO);
    }

    private void createUserIfNotExists(Long documento, String nombre, String apellido,
            String email, String password, AppRole role) {
        if (participanteRepository.existsById(documento)) {
            return; // User already exists
        }

        Optional<RolEntity> rolOpt = rolRepository.findByNombreRol(role.name());
        if (rolOpt.isEmpty()) {
            System.err.println("No se encontró el rol: " + role.name());
            return;
        }

        Set<RolEntity> roles = new HashSet<>();
        roles.add(rolOpt.get());

        ParticipanteEntity user = new ParticipanteEntity(
                documento,
                nombre,
                apellido,
                email,
                passwordEncoder.encode(password),
                roles);

        participanteRepository.save(user);
        System.out.println("Usuario creado: " + email + " con rol " + role.name());
    }
}
