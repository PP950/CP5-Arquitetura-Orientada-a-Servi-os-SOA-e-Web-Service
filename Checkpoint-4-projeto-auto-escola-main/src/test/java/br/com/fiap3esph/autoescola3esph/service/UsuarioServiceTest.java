package br.com.fiap3esph.autoescola3esph.service;

import br.com.fiap3esph.autoescola3esph.domain.usuario.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {
        when(passwordEncoder.encode("123456")).thenReturn("hash-da-senha");
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioResponseDTO resultado = service.cadastrarUsuario(
                new DadosCadastroUsuario("admin", "123456", Role.ADMIN));

        assertEquals("admin", resultado.login());
        assertEquals(Role.ADMIN, resultado.perfil());
        verify(passwordEncoder).encode("123456");
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class,
                () -> service.atualizarUsuario(99L, new DadosAtualizacaoUsuario("novo", Role.USER)));
    }

    @Test
    void deveExcluirUsuario() {
        Usuario usuario = new Usuario("joao", "hash", Role.USER);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        service.excluirUsuario(1L);

        verify(repository).delete(usuario);
    }

    @Test
    void deveAlterarSenhaQuandoSenhaAtualEstaCorreta() {
        Usuario usuario = new Usuario("joao", "hash-antigo", Role.USER);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(passwordEncoder.matches("atual", "hash-antigo")).thenReturn(true);
        when(passwordEncoder.encode("nova")).thenReturn("hash-novo");

        service.alterarSenha(authentication, new DadosAlteracaoSenha("atual", "nova"));

        assertEquals("hash-novo", usuario.getSenha());
        verify(repository).save(usuario);
    }

    @Test
    void deveRecusarAlteracaoQuandoSenhaAtualEstaErrada() {
        Usuario usuario = new Usuario("joao", "hash-antigo", Role.USER);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(passwordEncoder.matches("errada", "hash-antigo")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.alterarSenha(authentication, new DadosAlteracaoSenha("errada", "nova")));

        verify(repository, never()).save(any());
    }
}
