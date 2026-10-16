package br.com.vr.autorizador.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
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

    @Column(nullable = false, length = 16)
    private String numeroCartao;

    @Column(nullable = false)
    private Double valor;

    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

}
