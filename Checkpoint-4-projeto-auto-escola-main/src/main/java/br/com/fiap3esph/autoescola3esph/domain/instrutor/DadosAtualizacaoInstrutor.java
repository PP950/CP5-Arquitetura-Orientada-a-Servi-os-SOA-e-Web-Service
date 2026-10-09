package br.com.fiap3esph.autoescola3esph.domain.instrutor;

import br.com.fiap3esph.autoescola3esph.domain.endereco.DadosEndereco;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoInstrutor(
        @NotNull
        Long id,
        String nome,
        String email,
        String telefone,
        Especialidade especialidade,
        DadosEndereco endereco) {
}