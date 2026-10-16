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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
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

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        service = new TransacaoService(cartaoRepository, transacaoRepository, bCryptPasswordEncoder);
    }

    @Test
    void saveDebitaSaldoSalvaTransacaoERetornaDto() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, SENHA, BigDecimal.valueOf(125.0));
        Cartao cartao = new Cartao();
        cartao.setNumeroCartao(NUMERO_CARTAO);
        cartao.setSenha(bCryptPasswordEncoder.encode(SENHA));
        cartao.setSaldo(BigDecimal.valueOf(500.0));
        when(cartaoRepository.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.of(cartao));

        TransacaoDTO resultado = service.save(dto);

        assertSame(dto, resultado);
        assertEquals(BigDecimal.valueOf(375.0), cartao.getSaldo());
        verify(cartaoRepository).save(cartao);

        ArgumentCaptor<Transacao> transacaoCaptor = ArgumentCaptor.forClass(Transacao.class);
        verify(transacaoRepository).save(transacaoCaptor.capture());
        assertSame(cartao, transacaoCaptor.getValue().getCartao());
        assertEquals(BigDecimal.valueOf(125.0), transacaoCaptor.getValue().getValor());
    }

    @Test
    void saveRejeitaCartaoInexistenteSemPersistir() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, SENHA, BigDecimal.valueOf(125.0));
        when(cartaoRepository.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.empty());

        var exception = assertThrows(TransacaoService.TransacaoNegadaException.class, () -> service.save(dto));

        assertEquals("CARTAO_INEXISTENTE", exception.getCodigo());
        verify(cartaoRepository, never()).save(any());
        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void saveRejeitaSenhaInvalidaSemDebitarOuPersistir() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, "9999", BigDecimal.valueOf(125.0));
        Cartao cartao = cartaoComSaldo(500.0);
        when(cartaoRepository.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.of(cartao));

        var exception = assertThrows(TransacaoService.TransacaoNegadaException.class, () -> service.save(dto));

        assertEquals("SENHA_INVALIDA", exception.getCodigo());
        assertEquals(BigDecimal.valueOf(500.0), cartao.getSaldo());
        verify(cartaoRepository, never()).save(any());
        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void saveRejeitaSaldoInsuficienteIncluindoCompraQueZeraSaldo() {
        TransacaoDTO dto = new TransacaoDTO(NUMERO_CARTAO, SENHA, BigDecimal.valueOf(500.0));
        Cartao cartao = cartaoComSaldo(500.0);
        when(cartaoRepository.findByNumeroCartao(NUMERO_CARTAO)).thenReturn(Optional.of(cartao));

        var exception = assertThrows(TransacaoService.TransacaoNegadaException.class, () -> service.save(dto));

        assertEquals("SALDO_INSUFICIENTE", exception.getCodigo());
        assertEquals(BigDecimal.valueOf(500.0), cartao.getSaldo());
        verify(cartaoRepository, never()).save(any());
        verifyNoInteractions(transacaoRepository);
    }

    private Cartao cartaoComSaldo(double saldo) {
        Cartao cartao = new Cartao();
        cartao.setNumeroCartao(NUMERO_CARTAO);
        cartao.setSenha(bCryptPasswordEncoder.encode(SENHA));
        cartao.setSaldo(BigDecimal.valueOf(saldo));
        return cartao;
    }

    @Test
    void findByIdDelegaAoRepositorio() {
        UUID id = UUID.randomUUID();
        Transacao transacao = new Transacao();
        when(transacaoRepository.findById(id)).thenReturn(Optional.of(transacao));

        Optional<Transacao> resultado = service.findById(id);

        assertSame(transacao, resultado.orElseThrow());
        verify(transacaoRepository).findById(id);
    }

    @Test
    void deleteDelegaAoRepositorio() {
        Transacao transacao = new Transacao();

        service.delete(transacao);

        verify(transacaoRepository).delete(transacao);
    }
}
