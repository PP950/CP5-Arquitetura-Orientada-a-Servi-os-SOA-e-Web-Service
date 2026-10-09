package br.com.fiap3esph.autoescola3esph.domain.aluno;

import br.com.fiap3esph.autoescola3esph.domain.endereco.DadosEndereco;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoAluno(
        @NotNull
        Long id,
        String nome,

        @Email
        String email,
        String telefone,
        DadosEndereco endereco) {
}
