package br.com.franca.HelpDesk.repositorys;

import br.com.franca.HelpDesk.domains.Chamado;
import br.com.franca.HelpDesk.domains.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChamadosRepository extends JpaRepository<Chamado, Long> {


    Optional <Chamado> findById(Long id);



}
