package br.com.fiap3esph.autoescola3esph.service;

import br.com.fiap3esph.autoescola3esph.domain.instrutor.*;
import br.com.fiap3esph.autoescola3esph.domain.usuario.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UsuarioService {


    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO cadastrarUsuario(@Valid DadosCadastroUsuario dados) {
        Usuario usuario = new Usuario(
                dados.login(),
                passwordEncoder.encode(dados.senha()),
                dados.perfil()
        );

        Usuario saved = repository.save(usuario);
        return new UsuarioResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarUsuarios(Pageable paginacao) {
        return repository
                .findAll(paginacao)
                .map(UsuarioResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO detalharUsuario(Long id) {
        Usuario usuario = repository
                .findById(id)
                .orElseThrow(() ->
                        new InstrutorNotFoundException("ID do usuario informado não existe!"));
        return new UsuarioResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(
            Long id,
            DadosAtualizacaoUsuario dados) {

        Usuario usuario = repository
                .findById(id)
                .orElseThrow(() ->
                        new UsuarioNotFoundException("ID do usuário informado não existe!"));

        usuario.atualizarInformacoes(dados);

        Usuario saved = repository.save(usuario);

        return new UsuarioResponseDTO(saved);
    }

    public void excluirUsuario(Long id) {
        Usuario usuario = repository
                .findById(id)
                .orElseThrow(() ->
                        new UsuarioNotFoundException("ID do usuário informado não existe!"));

        repository.delete(usuario);
    }

    @Transactional
    public void alterarSenha(Authentication authentication, DadosAlteracaoSenha dados) {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        if (!passwordEncoder.matches(dados.senhaAtual(), usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta!");
        }

        usuario.setSenha(passwordEncoder.encode(dados.novaSenha()));

        repository.save(usuario);
    }
}