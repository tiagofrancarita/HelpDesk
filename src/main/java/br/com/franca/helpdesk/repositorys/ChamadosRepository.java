package br.com.franca.helpdesk.repositorys;

import br.com.franca.helpdesk.domains.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChamadosRepository extends JpaRepository<Chamado, Long> {


    Optional <Chamado> findById(Long id);



}
