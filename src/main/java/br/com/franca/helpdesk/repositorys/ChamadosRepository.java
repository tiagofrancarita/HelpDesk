package br.com.franca.helpdesk.repositorys;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Cliente;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChamadosRepository extends JpaRepository<Chamado, Long> {

    Optional <Chamado> findById(Long id);
    List<Chamado> findByTecnicoAndStatusEnum(Tecnico tecnico, StatusEnum status);
    List<Chamado> findByClienteAndStatusEnum(Cliente cliente, StatusEnum status);

    @Query(nativeQuery = true, value = "SELECT c.status_chamado, COUNT(c.status_chamado) as Contagem_Chamado FROM railway.chamado c GROUP BY c.status_chamado;")
    List<Object[]> countByStatus();

    List<Chamado> findByStatusEnumAndDataAberturaBefore(StatusEnum status, LocalDateTime dataAbertura);
}
