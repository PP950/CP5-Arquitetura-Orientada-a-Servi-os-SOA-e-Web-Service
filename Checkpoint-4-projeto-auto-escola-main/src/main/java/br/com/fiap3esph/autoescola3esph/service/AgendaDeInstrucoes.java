package br.com.fiap3esph.autoescola3esph.service;

import br.com.fiap3esph.autoescola3esph.domain.agenda.*;
import br.com.fiap3esph.autoescola3esph.domain.agenda.validacao.ValidadorAgendamento;
import br.com.fiap3esph.autoescola3esph.domain.agenda.validacao.ValidadorInstrutorAtivo;
import br.com.fiap3esph.autoescola3esph.domain.aluno.Aluno;
import br.com.fiap3esph.autoescola3esph.domain.aluno.AlunoNotFoundException;
import br.com.fiap3esph.autoescola3esph.domain.aluno.AlunoRepository;
import br.com.fiap3esph.autoescola3esph.domain.instrutor.Instrutor;
import br.com.fiap3esph.autoescola3esph.domain.instrutor.InstrutorNotFoundException;
import br.com.fiap3esph.autoescola3esph.domain.instrutor.InstrutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaDeInstrucoes {
    private final InstrucaoRepository repository;
    private final AlunoRepository alunoRepository;
    private final InstrutorRepository instrutorRepository;
    private final List<ValidadorAgendamento> validadoresAgendamento;

    @Transactional
    public DetalhamentoAgendamento agendar(DadosAgendamento dados) {
        if (!alunoRepository.existsById(dados.idAluno())) {
            throw new AlunoNotFoundException("Id do aluno informado não existe!");
        }
        if (dados.idInstrutor() != null && !instrutorRepository.existsById(dados.idInstrutor())) {
            throw new InstrutorNotFoundException("Id do instrutor informado não existe!");
        }

        //Validações
        validadoresAgendamento.forEach(validador -> validador.validar(dados));

        Aluno aluno = alunoRepository.getReferenceById(dados.idAluno());
        Instrutor instrutor = escolherInstrutor(dados);
        if (instrutor == null) {
            throw new ValidacaoException("Não há instrutor disponível para a data/hora escolhida!");
        }

        Instrucao instrucao = new Instrucao(aluno, instrutor, dados.dataHora());
        Instrucao salvo = repository.save(instrucao);
        return new DetalhamentoAgendamento(salvo);
    }

    private Instrutor escolherInstrutor(DadosAgendamento dados) {
        if (dados.idInstrutor() != null) {
            return instrutorRepository.getReferenceById(dados.idInstrutor());
        }
        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é obrigatória, caso o instrutor não seja informado!");
        }
        return instrutorRepository.escolherInstrutorAleatorioDisponivel(
                dados.especialidade(),
                dados.dataHora()
        );
    }

    @Transactional
    public void cancelar(Long id, DadosCancelamentoInstrucao dados) {

        Instrucao instrucao = repository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Instrução não encontrada!"));

        if (instrucao.getStatus() == StatusInstrucao.CANCELADA) {
            throw new ValidacaoException("A instrução já está cancelada!");
        }

        if (instrucao.getDataHora().isBefore(
                java.time.LocalDateTime.now().plusHours(24))) {
            throw new ValidacaoException(
                    "A instrução só pode ser cancelada com antecedência mínima de 24 horas!"
            );
        }

        instrucao.cancelar(dados.motivo());
    }

}