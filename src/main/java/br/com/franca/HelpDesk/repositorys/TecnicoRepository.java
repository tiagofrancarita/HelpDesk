package br.com.franca.HelpDesk.repositorys;

import br.com.franca.HelpDesk.domains.Cliente;
import br.com.franca.HelpDesk.domains.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

    Optional <Tecnico> findById(Long id);




}
