package br.com.franca.helpdesk.repositorys;

import br.com.franca.helpdesk.domains.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {



    Optional <Cliente> findById(Long id);
}
