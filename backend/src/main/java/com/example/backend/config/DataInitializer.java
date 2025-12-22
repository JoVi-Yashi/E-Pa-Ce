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
            Optional<RolEntity> rolOpt = rolRepository.findByNombreRol(appRole.name());
            if (rolOpt.isEmpty()) {
                RolEntity rol = new RolEntity(appRole.name());
                System.out.println("Rol creado: " + appRole.name());

                // Assign permissions based on requirements
                switch (appRole) {
                    case ADMIN:
                        // Wildcard
                        rol.getPermisos().add("ALL:ALL");
                        // Eventos
                        rol.getPermisos().add("EVENTO:CREATE");
                        rol.getPermisos().add("EVENTO:UPDATE_ALL");
                        rol.getPermisos().add("EVENTO:DELETE_ALL");
                        rol.getPermisos().add("EVENTO:READ_ALL");
                        rol.getPermisos().add("EVENTO:SHOW_QR");
                        rol.getPermisos().add("EVENTO:READ_PARTICIPANTS");
                        // Dashboard
                        rol.getPermisos().add("DASHBOARD:READ");
                        // Participantes
                        rol.getPermisos().add("PARTICIPANTE:CREATE");
                        rol.getPermisos().add("PARTICIPANTE:UPDATE_ALL");
                        rol.getPermisos().add("PARTICIPANTE:DELETE_ALL");
                        rol.getPermisos().add("PARTICIPANTE:READ_ALL");
                        // Certificados
                        rol.getPermisos().add("CERTIFICADO:EMITIR");
                        rol.getPermisos().add("CERTIFICADO:READ_ALL");
                        rol.getPermisos().add("CERTIFICADO:DELETE_ALL");
                        // Roles & Permissions
                        rol.getPermisos().add("ROL:MANAGE");
                        rol.getPermisos().add("ROL:VIEW");
                        // Auditoria
                        rol.getPermisos().add("AUDITORIA:VIEW");
                        // Configuracion (Atributos)
                        rol.getPermisos().add("CONFIGURACION:READ_ALL");
                        rol.getPermisos().add("CONFIGURACION:CREATE");
                        rol.getPermisos().add("CONFIGURACION:UPDATE_ALL");
                        rol.getPermisos().add("CONFIGURACION:DELETE_ALL");
                        rol.getPermisos().add("CONFIGURACION:MANAGE");
                        // Check-in
                        rol.getPermisos().add("CHECKIN:QR");
                        rol.getPermisos().add("CHECKIN:MANUAL");
                        rol.getPermisos().add("CHECKIN:READ_ALL");
                        rol.getPermisos().add("REPORTE:VIEW");
                        // Participaciones
                        rol.getPermisos().add("PARTICIPACION:CREATE");
                        rol.getPermisos().add("PARTICIPACION:DELETE_ALL");
                        rol.getPermisos().add("PARTICIPACION:READ_ALL");
                        break;
                    case OPERADOR:
                        rol.getPermisos().add("DASHBOARD:READ");
                        rol.getPermisos().add("EVENTO:READ_ALL");
                        rol.getPermisos().add("EVENTO:READ_OWN");
                        rol.getPermisos().add("EVENTO:CREATE");
                        rol.getPermisos().add("EVENTO:UPDATE_OWN");
                        rol.getPermisos().add("CHECKIN:QR");
                        rol.getPermisos().add("CHECKIN:MANUAL");
                        rol.getPermisos().add("CHECKIN:READ_OWN");
                        rol.getPermisos().add("REPORTE:VIEW");
                        rol.getPermisos().add("PARTICIPANTE:READ_ALL");
                        rol.getPermisos().add("PARTICIPANTE:CREATE");
                        rol.getPermisos().add("PARTICIPANTE:UPDATE_OWN");
                        rol.getPermisos().add("CERTIFICADO:READ_ALL");
                        rol.getPermisos().add("PARTICIPACION:READ_OWN");
                        rol.getPermisos().add("PARTICIPACION:CREATE");
                        rol.getPermisos().add("PARTICIPACION:DELETE_OWN");
                        break;
                    case MONITOR:
                        rol.getPermisos().add("EVENTO:READ_ALL");
                        rol.getPermisos().add("CHECKIN:QR");
                        rol.getPermisos().add("CHECKIN:READ_ALL");
                        break;
                    case INVITADO:
                        rol.getPermisos().add("EVENTO:READ");
                        rol.getPermisos().add("CHECKIN:QR");
                        rol.getPermisos().add("CHECKIN:READ_ALL");
                        rol.getPermisos().add("CERTIFICADO:DOWNLOAD");
                        rol.getPermisos().add("CERTIFICADO:VERIFY");
                        break;
                }

                rolRepository.save(rol);
            }
        }

        // 2. Create test users if they don't exist
        createUserIfNotExists(1000000001L, "Admin", "Sistema", "admin@epace.com", "admin123", AppRole.ADMIN);
        createUserIfNotExists(1000000002L, "Monitor", "Prueba", "monitor@epace.com", "test123", AppRole.MONITOR);
        createUserIfNotExists(1000000003L, "Operador", "Prueba", "operador@epace.com", "test123",
                AppRole.OPERADOR);
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
        user.setEstado("HABILITADO");

        participanteRepository.save(user);
        System.out.println("Usuario creado: " + email + " con rol " + role.name());
    }
}
