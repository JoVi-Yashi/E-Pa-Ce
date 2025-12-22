package com.example.backend.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private com.example.backend.auth.repository.ParticipanteRepository participanteRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String cleanEmail = email != null ? email.toLowerCase() : null;
        return participanteRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + cleanEmail));
    }
}