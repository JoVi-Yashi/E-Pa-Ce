package com.example.backend.auth.repository;

import com.example.backend.auth.entity.PasswordResetToken;
import com.example.backend.auth.entity.ParticipanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByParticipante(ParticipanteEntity participante);

    void deleteByParticipante(ParticipanteEntity participante);
}
