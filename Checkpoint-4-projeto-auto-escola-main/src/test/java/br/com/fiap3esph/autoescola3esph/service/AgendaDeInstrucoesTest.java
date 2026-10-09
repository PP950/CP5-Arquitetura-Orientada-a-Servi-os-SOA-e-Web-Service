package br.com.fiap3esph.autoescola3esph.service;

import br.com.fiap3esph.autoescola3esph.domain.agenda.*;
import br.com.fiap3esph.autoescola3esph.domain.aluno.Aluno;
import br.com.fiap3esph.autoescola3esph.domain.aluno.AlunoNotFoundException;
import br.com.fiap3esph.autoescola3esph.domain.aluno.AlunoRepository;
import br.com.fiap3esph.autoescola3esph.domain.instrutor.Especialidade;
import br.com.fiap3esph.autoescola3esph.domain.instrutor.Instrutor;
import br.com.fiap3esph.autoescola3esph.domain.instrutor.InstrutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaDeInstrucoesTest {

    @Mock
    private InstrucaoRepository repository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private InstrutorRepository instrutorRepository;

    private AgendaDeInstrucoes agenda;

    @BeforeEach
    void setUp() {
        // lista de validadores vazia para testar só a lógica do service
        agenda = new AgendaDeInstrucoes(repository, alunoRepository, instrutorRepository, List.of());
    }

    @Test
    void deveAgendarInstrucao() {
        Aluno aluno = new Aluno();
        aluno.setNome("Maria");
        Instrutor instrutor = new Instrutor();
        LocalDateTime dataHora = LocalDateTime.now().plusDays(2);

        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(instrutorRepository.existsById(2L)).thenReturn(true);
        when(alunoRepository.getReferenceById(1L)).thenReturn(aluno);
        when(instrutorRepository.getReferenceById(2L)).thenReturn(instrutor);
        when(repository.save(any(Instrucao.class))).thenAnswer(i -> i.getArgument(0));

        DadosAgendamento dados = new DadosAgendamento(1L, 2L, Especialidade.CARROS, dataHora);

        DetalhamentoAgendamento resultado = agenda.agendar(dados);

        assertEquals("Maria", resultado.nomeAluno());
        assertEquals(dataHora, resultado.dataHora());
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoExiste() {
        when(alunoRepository.existsById(1L)).thenReturn(false);

        DadosAgendamento dados = new DadosAgendamento(
                1L, 2L, Especialidade.CARROS, LocalDateTime.now().plusDays(2));

        assertThrows(AlunoNotFoundException.class, () -> agenda.agendar(dados));
    }

    @Test
    void deveCancelarInstrucaoComMaisDe24Horas() {
        Instrucao instrucao = new Instrucao(new Aluno(), new Instrutor(), LocalDateTime.now().plusDays(2));
        when(repository.findById(1L)).thenReturn(Optional.of(instrucao));

        agenda.cancelar(1L, new DadosCancelamentoInstrucao(MotivoCancelamento.ALUNO_DESISTIU));

        assertEquals(StatusInstrucao.CANCELADA, instrucao.getStatus());
        assertEquals(MotivoCancelamento.ALUNO_DESISTIU, instrucao.getMotivoCancelamento());
    }

    @Test
    void naoDeveCancelarInstrucaoComMenosDe24Horas() {
        Instrucao instrucao = new Instrucao(new Aluno(), new Instrutor(), LocalDateTime.now().plusHours(2));
        when(repository.findById(1L)).thenReturn(Optional.of(instrucao));

        assertThrows(ValidacaoException.class,
                () -> agenda.cancelar(1L, new DadosCancelamentoInstrucao(MotivoCancelamento.OUTROS)));

        assertEquals(StatusInstrucao.AGENDADA, instrucao.getStatus());
    }
}
