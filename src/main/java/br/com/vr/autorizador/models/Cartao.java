package br.com.vr.autorizador.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;

@Entity()
@Table(name = "cartao")
@Getter
@Setter
public class Cartao implements Serializable {

    private static final long serialVersionUID = 0L;

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 16, unique = true)
    private String numeroCartao;

    @Column(nullable = false, length = 4)
    private String senha;

    @Column(nullable = false)
    private Double saldo;
}
