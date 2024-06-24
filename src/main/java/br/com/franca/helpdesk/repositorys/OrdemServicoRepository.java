package br.com.franca.helpdesk.repositorys;

import br.com.franca.helpdesk.domains.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {


}