package br.com.vr.autorizador.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity()
@Table(name = "transacao")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Transacao implements Serializable {

    private static final long serialVersionUID = 0L;

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cartao_id", nullable = false)
    private Cartao cartao;

    @Column(nullable = false)
    private BigDecimal valor;

    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

}
