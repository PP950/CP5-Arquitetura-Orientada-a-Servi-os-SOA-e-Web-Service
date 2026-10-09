package br.com.fiap3esph.autoescola3esph.domain.usuario;

public record UsuarioResponseDTO(
        Long id,
        String login,
        Role perfil
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getPerfil()
        );
    }
}