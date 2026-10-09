package br.com.fiap3esph.autoescola3esph.service;

import br.com.fiap3esph.autoescola3esph.domain.endereco.DadosEndereco;
import br.com.fiap3esph.autoescola3esph.domain.instrutor.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstrutorServiceTest {

    @Mock
    private InstrutorRepository repository;

    @InjectMocks
    private InstrutorService service;

    private DadosCadastroInstrutor criarDados() {
        DadosEndereco endereco = new DadosEndereco(
                "Rua A", "10", null, "Centro", "São Paulo", "SP", "01310-100");
        return new DadosCadastroInstrutor(
                "João", "joao@email.com", "11999999999",
                "12345678901", Especialidade.CARROS, endereco);
    }

    @Test
    void deveCadastrarInstrutor() {
        when(repository.save(any(Instrutor.class))).thenAnswer(i -> i.getArgument(0));

        DadosDetalhamentoInstrutor resultado = service.cadastrarInstrutor(criarDados());

        assertEquals("João", resultado.nome());
        assertEquals(Especialidade.CARROS, resultado.especialidade());
        assertTrue(resultado.ativo());
        verify(repository).save(any(Instrutor.class));
    }

    @Test
    void deveLancarExcecaoQuandoInstrutorNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(InstrutorNotFoundException.class, () -> service.detalharInstrutor(99L));
    }

    @Test
    void deveAtualizarNomeDoInstrutor() {
        Instrutor instrutor = new Instrutor(criarDados());
        when(repository.findById(1L)).thenReturn(Optional.of(instrutor));
        when(repository.save(any(Instrutor.class))).thenAnswer(i -> i.getArgument(0));

        DadosAtualizacaoInstrutor dados =
                new DadosAtualizacaoInstrutor(1L, "João Silva", null, null, null, null);

        DadosDetalhamentoInstrutor resultado = service.atualizarInstrutor(dados);

        assertEquals("João Silva", resultado.nome());
        assertEquals("joao@email.com", resultado.email());
    }

    @Test
    void deveFazerExclusaoLogica() {
        Instrutor instrutor = new Instrutor(criarDados());
        when(repository.findById(1L)).thenReturn(Optional.of(instrutor));

        service.excluirInstrutor(1L);

        assertFalse(instrutor.isAtivo());
        verify(repository).save(instrutor);
    }
}
