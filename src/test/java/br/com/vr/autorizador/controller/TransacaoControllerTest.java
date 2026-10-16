package br.com.vr.autorizador.controller;

import br.com.vr.autorizador.dtos.TransacaoDTO;
import br.com.vr.autorizador.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {

    private static final String NUMERO_CARTAO = "1234567890123456";
    private static final String SENHA = "1234";

    @Mock
    private TransacaoService transacaoService;

    private TransacaoController controller;

    @BeforeEach
    void setUp() {
        controller = new TransacaoController(transacaoService);
    }

    @Test
    void incluirTransacaoComCartaoESenhaESaldoValidosRetornaCreated() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, SENHA, BigDecimal.valueOf(125.0));
        when(transacaoService.save(dto)).thenReturn(dto);

        ResponseEntity<Object> response = controller.incluirTransacao(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(dto, response.getBody());
        verify(transacaoService).save(dto);
    }

    @Test
    void incluirTransacaoComCartaoInexistenteRetornaErroCorrespondente() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, SENHA, BigDecimal.valueOf(125.0));
        when(transacaoService.save(dto)).thenThrow(new TransacaoService.TransacaoNegadaException("CARTAO_INEXISTENTE"));

        ResponseEntity<Object> response = controller.incluirTransacao(dto);

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertEquals("CARTAO_INEXISTENTE", response.getBody());
        verify(transacaoService).save(dto);
    }

    @Test
    void incluirTransacaoComSenhaInvalidaRetornaErroCorrespondente() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, "9999", BigDecimal.valueOf(125.0));
        when(transacaoService.save(dto)).thenThrow(new TransacaoService.TransacaoNegadaException("SENHA_INVALIDA"));

        ResponseEntity<Object> response = controller.incluirTransacao(dto);

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertEquals("SENHA_INVALIDA", response.getBody());
        verify(transacaoService).save(dto);
    }

    @Test
    void incluirTransacaoComSaldoInsuficienteRetornaErroCorrespondente() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, SENHA, BigDecimal.valueOf(500.0));
        when(transacaoService.save(dto)).thenThrow(new TransacaoService.TransacaoNegadaException("SALDO_INSUFICIENTE"));

        ResponseEntity<Object> response = controller.incluirTransacao(dto);

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertEquals("SALDO_INSUFICIENTE", response.getBody());
        verify(transacaoService).save(dto);
    }
}
