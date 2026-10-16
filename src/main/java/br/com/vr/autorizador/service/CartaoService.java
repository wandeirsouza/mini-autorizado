package br.com.vr.autorizador.service;

import br.com.vr.autorizador.repositories.CartaoRepository;
import br.com.vr.autorizador.dtos.CartaoDTO;
import br.com.vr.autorizador.models.Cartao;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartaoService {

    private final CartaoRepository cartaoRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Optional<Cartao> findById(UUID id) {
        return cartaoRepository.findById(id);
    }

    public CartaoDTO save(CartaoDTO cartaoDTO) {
        var cartao = new Cartao();
        cartao.setSaldo(500.0);
        BeanUtils.copyProperties(cartaoDTO, cartao);
        cartao.setSenha(bCryptPasswordEncoder.encode(cartaoDTO.senha()));
        cartaoRepository.save(cartao);
        return cartaoDTO;
    }

    public Cartao update(Cartao cartao) {
        return cartaoRepository.save(cartao);
    }

    public void delete(Cartao cartao) {
        cartaoRepository.delete(cartao);
    }

    @Transactional(rollbackOn = Exception.class)
    public Optional<Cartao> findByNumeroCartao(String numeroCartao){
        return cartaoRepository.findByNumeroCartao(numeroCartao);
    }

    public Optional<Cartao> findByNumeroCartaoAndSenha(String numeroCartao, String senha){
        return cartaoRepository.findByNumeroCartaoAndSenha(numeroCartao, bCryptPasswordEncoder.encode(senha));
    }

}
