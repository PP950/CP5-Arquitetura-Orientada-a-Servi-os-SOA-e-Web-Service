package br.com.fiap3esph.autoescola3esph.service;

import br.com.fiap3esph.autoescola3esph.domain.aluno.*;
import br.com.fiap3esph.autoescola3esph.domain.endereco.DadosEndereco;
import br.com.fiap3esph.autoescola3esph.domain.endereco.Endereco;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository repository;

    @InjectMocks
    private AlunoService service;

    private DadosEndereco criarEndereco() {
        return new DadosEndereco("Rua A", "10", null, "Centro", "São Paulo", "SP", "01310-100");
    }

    private Aluno criarAluno() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Maria");
        aluno.setEmail("maria@email.com");
        aluno.setTelefone("11988888888");
        aluno.setCpf("12345678900");
        aluno.setEndereco(new Endereco(criarEndereco()));
        aluno.setAtivo(true);
        return aluno;
    }

    @Test
    void deveCadastrarAlunoComoAtivo() {
        DadosCadastroAluno dados = new DadosCadastroAluno(
                "Maria", "maria@email.com", "11988888888", "12345678900", criarEndereco());

        service.cadastrar(dados);

        ArgumentCaptor<Aluno> captor = ArgumentCaptor.forClass(Aluno.class);
        verify(repository).save(captor.capture());
        Aluno salvo = captor.getValue();

        assertEquals("Maria", salvo.getNome());
        assertEquals("12345678900", salvo.getCpf());
        assertTrue(salvo.isAtivo());
    }

    @Test
    void deveRejeitarEmailInvalido() {
        DadosCadastroAluno dados = new DadosCadastroAluno(
                "Maria", "email-invalido", "11988888888", "12345678900", criarEndereco());

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            var violacoes = factory.getValidator().validate(dados);
            assertFalse(violacoes.isEmpty());
        }
    }

    @Test
    void deveListarApenasAlunosAtivos() {
        Page<Aluno> pagina = new PageImpl<>(List.of(criarAluno()));
        when(repository.findAllByAtivoTrue(any())).thenReturn(pagina);

        Page<DadosListagemAluno> resultado = service.listarAlunos(PageRequest.of(0, 10));

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Maria", resultado.getContent().get(0).nome());
    }

    @Test
    void deveDetalharAluno() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarAluno()));

        DadosDetalhamentoAluno resultado = service.detalharAluno(1L);

        assertEquals("Maria", resultado.nome());
        assertEquals("12345678900", resultado.cpf());
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AlunoNotFoundException.class, () -> service.detalharAluno(99L));
    }

    @Test
    void deveAtualizarNomeETelefoneDoAluno() {
        Aluno aluno = criarAluno();
        when(repository.findById(1L)).thenReturn(Optional.of(aluno));
        when(repository.save(any(Aluno.class))).thenAnswer(i -> i.getArgument(0));

        DadosAtualizacaoAluno dados =
                new DadosAtualizacaoAluno(1L, "Maria Silva", null, "11977777777", null);

        DadosDetalhamentoAluno resultado = service.atualizarAluno(dados);

        assertEquals("Maria Silva", resultado.nome());
        assertEquals("11977777777", resultado.telefone());
        assertEquals("maria@email.com", resultado.email());
    }

    @Test
    void deveFazerExclusaoLogica() {
        Aluno aluno = criarAluno();
        when(repository.findById(1L)).thenReturn(Optional.of(aluno));

        service.excluirAluno(1L);

        assertFalse(aluno.isAtivo());
        verify(repository).save(aluno);
    }
}
