package br.com.fiap3esph.autoescola3esph.domain.aluno;

import br.com.fiap3esph.autoescola3esph.domain.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DadosCadastroAluno(
        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String telefone,

        @NotBlank
        String cpf,

        @Valid
        DadosEndereco endereco
) {
}