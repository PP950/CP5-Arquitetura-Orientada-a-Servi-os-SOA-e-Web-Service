package br.com.fiap3esph.autoescola3esph.domain.usuario;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoUsuario(
        String login,
        Role perfil
) {
}