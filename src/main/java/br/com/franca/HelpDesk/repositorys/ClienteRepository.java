package br.com.franca.HelpDesk.repositorys;

import br.com.franca.HelpDesk.domains.Cliente;
import br.com.franca.HelpDesk.domains.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {



    Optional <Cliente> findById(Long id);
}
