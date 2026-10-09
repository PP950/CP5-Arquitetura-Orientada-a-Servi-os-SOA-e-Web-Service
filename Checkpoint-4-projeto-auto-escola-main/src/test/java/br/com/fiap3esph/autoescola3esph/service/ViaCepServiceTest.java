package br.com.fiap3esph.autoescola3esph.service;

import br.com.fiap3esph.autoescola3esph.domain.agenda.ValidacaoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ViaCepServiceTest {

    private final ViaCepService service =
            new ViaCepService("https://viacep.com.br/ws");

    @Test
    void deveRejeitarCepComTamanhoInvalido() {
        assertThrows(ValidacaoException.class, () -> service.buscarPorCep("123"));
    }

    @Test
    void deveRejeitarCepComLetras() {
        assertThrows(ValidacaoException.class, () -> service.buscarPorCep("abcdefgh"));
    }

    @Test
    void deveRejeitarCepNulo() {
        assertThrows(ValidacaoException.class, () -> service.buscarPorCep(null));
    }
}
