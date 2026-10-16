package br.com.vr.autorizador.dtos;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransacaoDTO(
        @NotBlank
        @Size(min = 16, max = 16)
        String numeroCartao,

        @NotBlank
        @Size(min = 4, max = 4)
        String senhaCartao,

        @NotNull
        @jakarta.validation.constraints.Positive
        BigDecimal valor
) {

}
