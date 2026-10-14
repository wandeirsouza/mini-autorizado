package br.com.vr.autorizador.dtos;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CartaoDTO (
        @NotBlank
        @Min(16)
        @Max(16)
        String numeroCartao,

        @NotBlank
        @Min(4)
        @Max(4)
        String senha
) {

}
