package com.example.backend.services;

import com.example.backend.persistence.entity.ParticipanteEntity;
import com.example.backend.repositories.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

        @Autowired
        ParticipanteRepository participanteRepository;

        @Override
        @Transactional
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                ParticipanteEntity participante = participanteRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User Not Found with email: " + email));

                return participante;
        }
}