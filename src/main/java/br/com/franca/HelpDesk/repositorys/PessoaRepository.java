package br.com.franca.HelpDesk.repositorys;

import br.com.franca.HelpDesk.domains.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {



    Optional <Pessoa> findById(Long id);
}
