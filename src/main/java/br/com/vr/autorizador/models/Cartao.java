package br.com.vr.autorizador.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity()
@Table(name = "cartao")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Cartao implements Serializable {

    private static final long serialVersionUID = 0L;

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 16, unique = true)
    private String numeroCartao;

    @Column(nullable = false, length = 100)
    private String senha;

    @Column(nullable = false)
    private BigDecimal saldo;

    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @OneToMany(mappedBy = "cartao")
    private List<Transacao> transacoes = new ArrayList<>();
}
