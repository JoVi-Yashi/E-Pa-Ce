package com.example.backend.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
@Data
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = ParticipanteEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "participante_id")
    private ParticipanteEntity participante;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, ParticipanteEntity participante) {
        this.token = token;
        this.participante = participante;
        this.expiryDate = LocalDateTime.now().plusHours(2); // Token válido por 2 horas
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
