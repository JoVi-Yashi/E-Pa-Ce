package com.example.backend.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad que representa las certificaciones emitidas a los participantes.
 * Incluye código de validación único y URL de descarga.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "certificaciones", indexes = {
        @Index(name = "idx_certificacion_participacion", columnList = "participacion_id"),
        @Index(name = "idx_certificacion_codigo", columnList = "codigo_validacion"),
        @Index(name = "idx_certificacion_emitido", columnList = "emitido"),
        @Index(name = "idx_certificacion_fecha", columnList = "fecha_emision")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_certificacion_codigo", columnNames = "codigo_validacion"),
        @UniqueConstraint(name = "uk_certificacion_participacion", columnNames = "participacion_id")
})
@EntityListeners(AuditingEntityListener.class)
public class CertificacionesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull(message = "La participación es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participacion_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_certificacion_participacion"))
    private ParticipacionEntity participacion;

    @Builder.Default
    @Column(name = "codigo_validacion", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID codigoValidacion = UUID.randomUUID();

    @CreatedDate
    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private OffsetDateTime fechaEmision;

    @Size(max = 500, message = "La URL de descarga no puede exceder 500 caracteres")
    @Column(name = "url_descarga", length = 500)
    private String urlDescarga;

    @Builder.Default
    @ColumnDefault("false")
    @Column(name = "emitido", nullable = false)
    private Boolean emitido = false;

    @Size(max = 40, message = "El código único de API no puede exceder 40 caracteres")
    @Column(name = "codigo_unico_api", length = 40)
    private String codigoUnicoApi;

    /**
     * Marca la certificación como emitida y establece la URL de descarga.
     */
    public void marcarComoEmitida(String urlDescarga) {
        this.emitido = true;
        this.urlDescarga = urlDescarga;
    }

    /**
     * Verifica si la certificación ya fue emitida.
     */
    public boolean estaEmitida() {
        return Boolean.TRUE.equals(this.emitido);
    }

}