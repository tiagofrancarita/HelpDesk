package br.com.franca.helpdesk.repositorys;

import br.com.franca.helpdesk.domains.Pessoa;
import br.com.franca.helpdesk.domains.dtos.TecnicoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    Optional <Pessoa> findById(Long id);

    Optional<Pessoa> findByCpfAndEmail(String cpf, String email);

    Optional<Pessoa> findByEmail(String email);

    Optional<Pessoa> findByCpf(String cpf);
}