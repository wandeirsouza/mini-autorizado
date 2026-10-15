package br.com.vr.autorizador.service;

import br.com.vr.autorizador.dtos.CartaoDTO;
import br.com.vr.autorizador.dtos.TransacaoDTO;
import br.com.vr.autorizador.models.Cartao;
import br.com.vr.autorizador.models.Transacao;
import br.com.vr.autorizador.repositories.CartaoRepository;
import br.com.vr.autorizador.repositories.TransacaoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransacaoService {

    private final CartaoRepository cartaoRepository;

    private final TransacaoRepository transacaoRepository;

    public Optional<Cartao> findById(UUID id) {
        return cartaoRepository.findById(id);
    }

    @Transactional(rollbackOn = Exception.class)
    public TransacaoDTO save(TransacaoDTO transacaoDTO) {
        Optional<Cartao> cartao  = cartaoRepository.findByNumeroCartao(transacaoDTO.numeroCartao());
        Cartao cartaoRetornado = cartao.get();
        cartaoRetornado.setSaldo(cartaoRetornado.getSaldo()-transacaoDTO.valor());
        cartaoRepository.save(cartaoRetornado);
        var transacao = new Transacao();
        BeanUtils.copyProperties(transacaoDTO, transacao);
        transacaoRepository.save(transacao);
        return transacaoDTO;
    }

    public void delete(Transacao transacao) {
        transacaoRepository.delete(transacao);
    }


}
