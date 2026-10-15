package br.com.vr.autorizador.controller;

import br.com.vr.autorizador.dtos.TransacaoDTO;
import br.com.vr.autorizador.models.Cartao;
import br.com.vr.autorizador.service.CartaoService;
import br.com.vr.autorizador.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {

    private static final String NUMERO_CARTAO = "1234567890123456";
    private static final String SENHA = "1234";

    @Mock
    private CartaoService cartaoService;

    @Mock
    private TransacaoService transacaoService;

    private TransacaoController controller;

    @BeforeEach
    void setUp() {
        controller = new TransacaoController(cartaoService, transacaoService);
    }

    @Test
    void incluirTransacaoComCartaoESenhaESaldoValidosRetornaCreated() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, SENHA, 125.0);
        Cartao cartao = cartao(SENHA, 500.0);
        when(cartaoService.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.of(cartao));
        when(transacaoService.save(dto)).thenReturn(dto);

        ResponseEntity<Object> response = controller.incluirTransacao(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(dto, response.getBody());
        verify(transacaoService).save(dto);
    }

    @Test
    void incluirTransacaoComCartaoInexistenteRetornaErroCorrespondente() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, SENHA, 125.0);
        when(cartaoService.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = controller.incluirTransacao(dto);

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertEquals("CARTAO_INEXISTENTE", response.getBody());
        verifyNoInteractions(transacaoService);
    }

    @Test
    void incluirTransacaoComSenhaInvalidaRetornaErroCorrespondente() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, "9999", 125.0);
        when(cartaoService.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.of(cartao(SENHA, 500.0)));

        ResponseEntity<Object> response = controller.incluirTransacao(dto);

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertEquals("SENHA_INVALIDA", response.getBody());
        verifyNoInteractions(transacaoService);
    }

    @Test
    void incluirTransacaoComSaldoInsuficienteRetornaErroCorrespondente() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, SENHA, 500.0);
        when(cartaoService.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.of(cartao(SENHA, 500.0)));

        ResponseEntity<Object> response = controller.incluirTransacao(dto);

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertEquals("SALDO_INSUFICIENTE", response.getBody());
        verifyNoInteractions(transacaoService);
    }

    private Cartao cartao(String senha, double saldo) {
        Cartao cartao = new Cartao();
        cartao.setNumeroCartao(NUMERO_CARTAO);
        cartao.setSenha(senha);
        cartao.setSaldo(saldo);
        return cartao;
    }
}
