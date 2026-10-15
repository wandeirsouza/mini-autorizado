package br.com.vr.autorizador.dtos;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CartaoDTO (
        @NotBlank
        @Size(max = 16)
        String numeroCartao,

        @NotBlank
        @Size(max = 4)
        String senha
) {

}
