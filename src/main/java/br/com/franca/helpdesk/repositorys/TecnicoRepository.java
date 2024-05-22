package br.com.franca.helpdesk.repositorys;

import br.com.franca.helpdesk.domains.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

    Optional <Tecnico> findById(Long id);

    Optional<Tecnico> findByEmail(String email);




}
