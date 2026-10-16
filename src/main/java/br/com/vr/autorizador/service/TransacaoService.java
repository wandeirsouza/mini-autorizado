package br.com.vr.autorizador.service;

import br.com.vr.autorizador.dtos.TransacaoDTO;
import br.com.vr.autorizador.models.Cartao;
import br.com.vr.autorizador.models.Transacao;
import br.com.vr.autorizador.repositories.CartaoRepository;
import br.com.vr.autorizador.repositories.TransacaoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransacaoService {

    private final CartaoRepository cartaoRepository;

    private final TransacaoRepository transacaoRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Optional<Transacao> findById(UUID id) {
        return transacaoRepository.findById(id);
    }

    @Transactional
    public TransacaoDTO save(TransacaoDTO transacaoDTO) {
        Optional.ofNullable(transacaoDTO.valor())
                .filter(valor -> valor.doubleValue() > 0)
                .orElseThrow(() -> new TransacaoNegadaException("VALOR_INVALIDO"));

        Cartao cartaoRetornado = cartaoRepository.findByNumeroCartao(transacaoDTO.numeroCartao())
                .orElseThrow(() -> new TransacaoNegadaException("CARTAO_INEXISTENTE"));

        Optional.of(cartaoRetornado)
                .filter(cartao ->  bCryptPasswordEncoder.matches(transacaoDTO.senhaCartao(), cartao.getSenha()))
                .orElseThrow(() -> new TransacaoNegadaException("SENHA_INVALIDA"));

        Optional.of(cartaoRetornado)
                .filter(cartao -> cartao.getSaldo().doubleValue() >= transacaoDTO.valor().doubleValue())
                .orElseThrow(() -> new TransacaoNegadaException("SALDO_INSUFICIENTE"));

        cartaoRetornado.setSaldo(cartaoRetornado.getSaldo().subtract(transacaoDTO.valor()));
        cartaoRepository.save(cartaoRetornado);
        var transacao = new Transacao();
        transacao.setNumeroCartao(transacaoDTO.numeroCartao());
        transacao.setValor(transacaoDTO.valor());
        transacaoRepository.save(transacao);
        return transacaoDTO;
    }

    public static class TransacaoNegadaException extends RuntimeException {
        private final String codigo;

        public TransacaoNegadaException(String codigo) {
            super(codigo);
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }
    }

    public void delete(Transacao transacao) {
        transacaoRepository.delete(transacao);
    }


}
