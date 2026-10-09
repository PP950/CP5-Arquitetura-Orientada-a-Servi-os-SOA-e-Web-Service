package br.com.fiap3esph.autoescola3esph.domain.endereco;

public class CepNaoEncontradoException extends RuntimeException {
    public CepNaoEncontradoException(String message) {
        super(message);
    }
}
