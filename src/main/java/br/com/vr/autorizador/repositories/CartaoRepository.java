package br.com.vr.autorizador.repositories;

import br.com.vr.autorizador.models.Cartao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, UUID> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Cartao> findByNumeroCartao(String numeroCartao);

    Optional<Cartao> findByNumeroCartaoAndSenha(String numeroCartao, String senha);
}