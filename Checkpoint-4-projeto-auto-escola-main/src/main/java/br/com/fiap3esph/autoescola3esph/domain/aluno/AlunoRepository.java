package br.com.fiap3esph.autoescola3esph.domain.aluno;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    boolean existsByIdAndAtivoFalse(Long id);

    Page<Aluno> findAllByAtivoTrue(Pageable paginacao);
}