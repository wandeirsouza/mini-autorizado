package br.com.vr.autorizador.service;

import br.com.vr.autorizador.dtos.TransacaoDTO;
import br.com.vr.autorizador.models.Cartao;
import br.com.vr.autorizador.models.Transacao;
import br.com.vr.autorizador.repositories.CartaoRepository;
import br.com.vr.autorizador.repositories.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    private static final String NUMERO_CARTAO = "1234567890123456";
    private static final String SENHA = "1234";

    @Mock
    private CartaoRepository cartaoRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    private TransacaoService service;

    @BeforeEach
    void setUp() {
        service = new TransacaoService(cartaoRepository, transacaoRepository);
    }

    @Test
    void saveDebitaSaldoSalvaTransacaoERetornaDto() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, SENHA, 125.0);
        Cartao cartao = new Cartao();
        cartao.setNumeroCartao(NUMERO_CARTAO);
        cartao.setSaldo(500.0);
        when(cartaoRepository.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.of(cartao));

        TransacaoDTO resultado = service.save(dto);

        assertSame(dto, resultado);
        assertEquals(375.0, cartao.getSaldo());
        verify(cartaoRepository).save(cartao);

        ArgumentCaptor<Transacao> transacaoCaptor = ArgumentCaptor.forClass(Transacao.class);
        verify(transacaoRepository).save(transacaoCaptor.capture());
        assertEquals(NUMERO_CARTAO, transacaoCaptor.getValue().getNumeroCartao());
        assertEquals(125.0, transacaoCaptor.getValue().getValor());
    }

    @Test
    void findByIdDelegaAoRepositorio() {
        UUID id = UUID.randomUUID();
        Cartao cartao = new Cartao();
        when(cartaoRepository.findById(id)).thenReturn(Optional.of(cartao));

        Optional<Cartao> resultado = service.findById(id);

        assertSame(cartao, resultado.orElseThrow());
        verify(cartaoRepository).findById(id);
    }

    @Test
    void deleteDelegaAoRepositorio() {
        Transacao transacao = new Transacao();

        service.delete(transacao);

        verify(transacaoRepository).delete(transacao);
    }
}
