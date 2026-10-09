package br.com.fiap3esph.autoescola3esph.domain.agenda;

import jakarta.validation.constraints.NotNull;

public record DadosCancelamentoInstrucao(
        @NotNull
        MotivoCancelamento motivo
) {}