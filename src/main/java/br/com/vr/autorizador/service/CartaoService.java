package br.com.vr.autorizador.service;

import br.com.vr.autorizador.repositories.CartaoRepository;
import br.com.vr.autorizador.dtos.CartaoDTO;
import br.com.vr.autorizador.models.Cartao;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartaoService {

    private final CartaoRepository cartaoRepository;

    public Optional<Cartao> findById(UUID id) {
        return cartaoRepository.findById(id);
    }

    public CartaoDTO save(CartaoDTO cartaoDTO) {
        var cartao = new Cartao();
        cartao.setSaldo(500.0);
        BeanUtils.copyProperties(cartaoDTO, cartao);
        cartaoRepository.save(cartao);
        return cartaoDTO;
    }

    public Cartao update(Cartao cartao) {
        return cartaoRepository.save(cartao);
    }

    public void delete(Cartao cartao) {
        cartaoRepository.delete(cartao);
    }

    public Optional<Cartao> findByNumeroCartao(String numeroCartao){
        return cartaoRepository.findByNumeroCartao(numeroCartao);
    }

    public Optional<Cartao> findByNumeroCartaoAndSenha(String numeroCartao, String senha){
        return cartaoRepository.findByNumeroCartaoAndSenha(numeroCartao, senha);
    }

}
