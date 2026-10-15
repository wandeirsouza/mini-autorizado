package br.com.vr.autorizador.controller;

import br.com.vr.autorizador.dtos.TransacaoDTO;
import br.com.vr.autorizador.models.Cartao;
import br.com.vr.autorizador.service.CartaoService;
import br.com.vr.autorizador.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/transacoes")
@AllArgsConstructor
public class TransacaoController {

    private final CartaoService cartaoService;
    private final TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<Object> incluirTransacao(@RequestBody @Valid TransacaoDTO transacaoDTO) {
        return cartaoService.findByNumeroCartao(transacaoDTO.numeroCartao())
                .<ResponseEntity<Object>>map(cartao -> validarSenha(cartao, transacaoDTO))
                .orElseGet(() -> respostaErro("CARTAO_INEXISTENTE"));
    }

    private ResponseEntity<Object> validarSenha(Cartao cartao, TransacaoDTO transacaoDTO) {
        return Optional.of(cartao)
                .filter(cartaoEncontrado -> cartaoEncontrado.getSenha().equals(transacaoDTO.senhaCartao()))
                .map(cartaoEncontrado -> validarSaldo(cartaoEncontrado, transacaoDTO))
                .orElseGet(() -> respostaErro("SENHA_INVALIDA"));
    }

    private ResponseEntity<Object> validarSaldo(Cartao cartao, TransacaoDTO transacaoDTO) {
        return Optional.of(cartao)
                .filter(cartaoEncontrado -> cartaoEncontrado.getSaldo() > transacaoDTO.valor())
                .map(cartaoEncontrado -> salvaTransacao(transacaoDTO))
                .orElseGet(() -> respostaErro("SALDO_INSUFICIENTE"));
    }

    private ResponseEntity<Object> respostaErro(String mensagem) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(mensagem);
    }

    private ResponseEntity<Object> salvaTransacao(TransacaoDTO transacaoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transacaoService.save(transacaoDTO));
    }
}