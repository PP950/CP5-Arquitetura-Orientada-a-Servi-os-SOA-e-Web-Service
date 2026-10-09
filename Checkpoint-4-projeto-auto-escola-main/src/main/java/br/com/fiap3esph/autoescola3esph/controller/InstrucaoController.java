package br.com.fiap3esph.autoescola3esph.controller;

import br.com.fiap3esph.autoescola3esph.domain.agenda.*;
import br.com.fiap3esph.autoescola3esph.domain.aluno.Aluno;
import br.com.fiap3esph.autoescola3esph.domain.aluno.AlunoRepository;
import br.com.fiap3esph.autoescola3esph.domain.instrutor.Instrutor;
import br.com.fiap3esph.autoescola3esph.domain.instrutor.InstrutorRepository;
import br.com.fiap3esph.autoescola3esph.service.AgendaDeInstrucoes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instrucoes")
@RequiredArgsConstructor
public class InstrucaoController {
    private final AgendaDeInstrucoes agenda;

    @PostMapping
    public ResponseEntity agendarInstrucao(@RequestBody @Valid DadosAgendamento dados) {
        return ResponseEntity.ok(agenda.agendar(dados));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarInstrucao(
            @PathVariable Long id,
            @RequestBody @Valid DadosCancelamentoInstrucao dados) {

        agenda.cancelar(id, dados);

        return ResponseEntity.noContent().build();
    }

}