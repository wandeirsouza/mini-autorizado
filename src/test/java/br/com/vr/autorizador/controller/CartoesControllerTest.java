package br.com.vr.autorizador.controller;

import br.com.vr.autorizador.dtos.CartaoDTO;
import br.com.vr.autorizador.models.Cartao;
import br.com.vr.autorizador.service.CartaoService;
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
class CartoesControllerTest {

    private static final String NUMERO_CARTAO = "1234567890123456";
    private static final String SENHA = "1234";

    @Mock
    private CartaoService cartaoService;

    private CartoesController controller;

    @BeforeEach
    void setUp() {
        controller = new CartoesController(cartaoService);
    }

    @Test
    void saveCartaoQuandoCartaoExistenteRetornaCreatedEDtoSalvo() {
        CartaoDTO dto = new CartaoDTO(NUMERO_CARTAO, SENHA);
        Cartao cartaoExistente = new Cartao();
        when(cartaoService.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.of(cartaoExistente));
        when(cartaoService.save(dto)).thenReturn(dto);

        ResponseEntity<CartaoDTO> response = controller.saveCartao(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(dto, response.getBody());
        verify(cartaoService).findByNumeroCartao(NUMERO_CARTAO);
        verify(cartaoService).save(dto);
    }

    @Test
    void saveCartaoQuandoCartaoNaoExisteRetornaUnprocessableContent() {
        CartaoDTO dto = new CartaoDTO(NUMERO_CARTAO, SENHA);
        when(cartaoService.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.empty());

        ResponseEntity<CartaoDTO> response = controller.saveCartao(dto);

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertSame(dto, response.getBody());
        verify(cartaoService).findByNumeroCartao(NUMERO_CARTAO);
        verify(cartaoService, never()).save(any());
    }

    @Test
    void updateCartaoQuandoCartaoExisteRetornaSaldo() {
        Cartao cartao = new Cartao();
        cartao.setSaldo(500.0);
        when(cartaoService.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.of(cartao));

        ResponseEntity<Object> response = controller.updateCartao(NUMERO_CARTAO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(500.0, response.getBody());
        verify(cartaoService).findByNumeroCartao(NUMERO_CARTAO);
    }

    @Test
    void updateCartaoQuandoCartaoNaoExisteRetornaNotFound() {
        when(cartaoService.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = controller.updateCartao(NUMERO_CARTAO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(cartaoService).findByNumeroCartao(NUMERO_CARTAO);
    }
}
