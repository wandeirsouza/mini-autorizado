package br.com.vr.autorizador.repositories;

import br.com.vr.autorizador.models.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {
    Optional<Transacao> findByNumeroCartao(String numeroCartao);
}
