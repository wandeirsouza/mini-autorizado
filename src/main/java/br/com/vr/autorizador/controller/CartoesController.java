package br.com.vr.autorizador.controller;

import br.com.vr.autorizador.dtos.CartaoDTO;
import br.com.vr.autorizador.models.Cartao;
import br.com.vr.autorizador.service.CartaoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/cartoes")
@AllArgsConstructor
public class CartoesController {

    private final CartaoService cartaoService;

    @PostMapping
    public ResponseEntity<CartaoDTO> saveCartao(@RequestBody @Valid CartaoDTO cartaoDTO) {
        Optional<Cartao> cartao  = cartaoService.findByNumeroCartao(cartaoDTO.numeroCartao());
        return cartao.<ResponseEntity<CartaoDTO>>map(value -> ResponseEntity.status(HttpStatus.CREATED)
                .body(cartaoService.save(cartaoDTO))).orElseGet(() -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(cartaoDTO));
    }

    @GetMapping(value = "/{numeroCartcao}")
    public ResponseEntity<Object> updateCartao(@PathVariable(value = "numeroCartcao") String numeroCartao) {
        Optional<Cartao> cartao = cartaoService.findByNumeroCartao(numeroCartao);
        return cartao.<ResponseEntity<Object>>map(value -> ResponseEntity.status(HttpStatus.OK)
                .body(value.getSaldo())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
