package br.com.vr.autorizador.controller;

import br.com.vr.autorizador.dtos.TransacaoDTO;
import br.com.vr.autorizador.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/transacoes")
@AllArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<Object> incluirTransacao(@RequestBody @Valid TransacaoDTO transacaoDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transacaoService.save(transacaoDTO));
        } catch (TransacaoService.TransacaoNegadaException e) {
            return respostaErro(e.getCodigo());
        }
    }

    private ResponseEntity<Object> respostaErro(String mensagem) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(mensagem);
    }

}
