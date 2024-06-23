package br.com.franca.helpdesk.usecases;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Cliente;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.dtos.ChamadosDTO;
import br.com.franca.helpdesk.domains.dtos.ClienteDTO;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.exceptions.ChamadoStatusUpdateException;
import br.com.franca.helpdesk.exceptions.ClienteAndChamadosNotDeleted;
import br.com.franca.helpdesk.exceptions.DataIntegrityViolationException;
import br.com.franca.helpdesk.exceptions.ObjectnotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.ClienteRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChamadoUseCase {

    private Logger log = LoggerFactory.getLogger(ChamadoUseCase.class);
    private final ChamadosRepository chamadosRepository;
    private final TecnicosUseCase tecnicosUseCase;
    private final ClienteUseCase clienteUseCase;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;;

    @Autowired
    public ChamadoUseCase(ChamadosRepository chamadosRepository, TecnicosUseCase tecnicosUseCase, ClienteUseCase clienteUseCase, ClienteRepository clienteRepository, TecnicoRepository tecnicoRepository) {
        this.chamadosRepository = chamadosRepository;
        this.tecnicosUseCase = tecnicosUseCase;
        this.clienteUseCase = clienteUseCase;
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    public ResponseEntity<ChamadosDTO> buscarChamadoPorId(Long id) {
        log.error("---- Iniciando a busca de chamado por ID.... ----");
        if (id <= 0) {
            throw new IllegalArgumentException("O id informado é invalido");
        }

        Optional<Chamado> buscaChamadoID = chamadosRepository.findById(id);

        if (buscaChamadoID.isEmpty()) {
            log.error("---- Chamado não encontrado. ----ID: " + id);
            throw new ObjectnotFoundException("Nenhum chamado encontrado");
        }

        Chamado chamadoEncontrado = buscaChamadoID.get();
        log.info("---- Cliente encontrado. ----ID: " + chamadoEncontrado.getId());
        return ResponseEntity.ok(new ChamadosDTO(chamadoEncontrado));
    }

    public List<ChamadosDTO> listarChamados() {
        log.info("---- Iniciando a listagem de chamados cadastrados.... ----");
        List<Chamado> chamados = chamadosRepository.findAll();
        if (chamados.isEmpty()) {
            log.error("Nenhum chamado encontrado");
            throw new ObjectnotFoundException("Nenhum chamado encontrado");
        }
        List<ChamadosDTO> chamadoDTO = chamados.stream()
                .map(ChamadosDTO::new)
                .collect(Collectors.toList());
        log.info("---- Chamados listados com sucesso. ----");
        return chamadoDTO;
    }

    public Chamado cadastrarChamado(ChamadosDTO chamadosDTO) {

        log.info("---- Iniciando o cadastro de chamado... ----");

        try {
            log.info("---- Iniciando validação dos dados informados... ----");
            validaDadosInformados(chamadosDTO);
            log.info("---- Dados validados com sucesso. ----");

            Chamado ChamadoSalvo = new Chamado(chamadosDTO);

            log.info("---- Chamado salvo com sucesso ----");

            return chamadosRepository.save(ChamadoSalvo);

        }catch (Exception e) {
            log.error("---- Erro ao cadastrar o cliente. ----");
            throw new DataIntegrityViolationException("Erro ao cadastrar o cliente");
        }
    }

    public ChamadosDTO deletaChamadoPorId(Long id) {

        log.info("---- Iniciando processo de deleção de chamado por id ----");

        if (id == null || id <= 0) {
            log.error("---- Erro ao deletar chamado, id informado é inválido ----");
            throw new ObjectnotFoundException("Id Invalido");
        }

        log.info("---- Verificando se há chamados abertos atribuídos a clientes ou técnicos ----");

        // Verifica se o chamado existe
        Optional<Chamado> buscarChamado = chamadosRepository.findById(id);
        if (!buscarChamado.isPresent()) {
            log.error("---- Chamado não encontrado ----");
            throw new ObjectnotFoundException("Chamado não encontrado");
        }
        Chamado chamadoEncontrado = buscarChamado.get();

        // Verifica se o chamado está aberto
        if (chamadoEncontrado.getStatusEnum() == StatusEnum.ABERTO) {

            // Verifica se o chamado está associado a um cliente Verifica se o chamado está associado a um técnico
            Cliente clienteEncontrado = chamadoEncontrado.getCliente();
            Tecnico tecnicoEncontrado = chamadoEncontrado.getTecnico();
            if (clienteEncontrado != null && tecnicoEncontrado != null) {
                log.error(String.format("---- Não é possível excluir o chamado com ID %d. O chamado está associado ao cliente com ID %d,  e ao técnico com ID %d ----", chamadoEncontrado.getId(), clienteEncontrado.getId(),tecnicoEncontrado.getId()));
                throw new ClienteAndChamadosNotDeleted(String.format("Não é possível excluir o chamado com ID %d. O chamado está associado ao cliente com ID %d  e ao técnico com ID %d ----", chamadoEncontrado.getId(), clienteEncontrado.getId(),tecnicoEncontrado.getId()));
            }
        }

        log.info("---- Excluindo chamado.... ----");
        chamadosRepository.delete(chamadoEncontrado);
        log.info("---- Chamado excluído ----");

        return new ChamadosDTO(chamadoEncontrado);
    }

    public ChamadosDTO atualizarChamadoParaStatusEmExecucao(Long id) {

        log.info("---- Iniciando processo de atualização de status de chamado para Em Execução ----");

        if (id == null || id <= 0) {
            log.error("---- Erro ao atualizar chamado, id informado é inválido ----");
            throw new ObjectnotFoundException("Id Invalido");
        }

        // Verifica se o chamado existe
        Optional<Chamado> buscarChamado = chamadosRepository.findById(id);
        if (!buscarChamado.isPresent()) {
            log.error("---- Chamado não encontrado ----");
            throw new ObjectnotFoundException("Chamado não encontrado");
        }
        Chamado chamadoEncontrado = buscarChamado.get();

        // Verifica se o chamado já está em execução ou concluído
        if (chamadoEncontrado.getStatusEnum() == StatusEnum.EXECUÇÃO || chamadoEncontrado.getStatusEnum() == StatusEnum.ENCERRADO || chamadoEncontrado.getStatusEnum() == StatusEnum.DEVOLVIDO || chamadoEncontrado.getStatusEnum() == StatusEnum.CANCELADO) {
            log.error(String.format("---- Não é possível atualizar o status do chamado com ID %d, pois ele já está em execução, concluído, devolvido ou cancelado ----", chamadoEncontrado.getId()));
            throw new ChamadoStatusUpdateException(String.format("Não é possível atualizar o status do chamado com ID %d, pois ele já está em execução, concluído, devolvido ou cancelado", chamadoEncontrado.getId()));
        }

        // Atualiza o status do chamado para Em Execução
        chamadoEncontrado.setStatusEnum(StatusEnum.EXECUÇÃO);
        chamadosRepository.save(chamadoEncontrado);

        log.info("---- Status do chamado atualizado para Em Execução ----");

        // Retorna DTO do chamado atualizado
        return new ChamadosDTO(chamadoEncontrado);
    }

    public ChamadosDTO atualizarChamadoParaStatusEncerrado(Long id) {

        log.info("---- Iniciando processo de atualização de status de chamado para encerrado  ----");

        if (id == null || id <= 0) {
            log.error("---- Erro ao atualizar chamado, id informado é inválido ----");
            throw new ObjectnotFoundException("Id Invalido");
        }

        // Verifica se o chamado existe
        Optional<Chamado> buscarChamado = chamadosRepository.findById(id);
        if (!buscarChamado.isPresent()) {
            log.error("---- Chamado não encontrado ----");
            throw new ObjectnotFoundException("Chamado não encontrado");
        }
        Chamado chamadoEncontrado = buscarChamado.get();

        // Verifica se o chamado já está encerrado ou devolvido
        if (chamadoEncontrado.getStatusEnum() == StatusEnum.ENCERRADO || chamadoEncontrado.getStatusEnum() == StatusEnum.DEVOLVIDO || chamadoEncontrado.getStatusEnum() == StatusEnum.CANCELADO) {
            log.error(String.format("---- Não é possível atualizar o status do chamado com ID %d, pois ele já está encerrado, devolvido ou cancelado ----", chamadoEncontrado.getId()));
            throw new ChamadoStatusUpdateException(String.format("Não é possível atualizar o status do chamado com ID %d, pois ele já está encerrado, devolvido ou cancelado", chamadoEncontrado.getId()));
        }

        // Atualiza o status do chamado para Em Execução
        chamadoEncontrado.setStatusEnum(StatusEnum.ENCERRADO);
        chamadosRepository.save(chamadoEncontrado);

        log.info("---- Status do chamado atualizado para encerrado ----");

        // Retorna DTO do chamado atualizado
        return new ChamadosDTO(chamadoEncontrado);
    }

    public ChamadosDTO atualizarChamadoParaStatusCancelado(Long id) {

        log.info("---- Iniciando processo de atualização de status de chamado para cancelado  ----");

        if (id == null || id <= 0) {
            log.error("---- Erro ao atualizar chamado, id informado é inválido ----");
            throw new ObjectnotFoundException("Id Invalido");
        }

        // Verifica se o chamado existe
        Optional<Chamado> buscarChamado = chamadosRepository.findById(id);
        if (!buscarChamado.isPresent()) {
            log.error("---- Chamado não encontrado ----");
            throw new ObjectnotFoundException("Chamado não encontrado");
        }
        Chamado chamadoEncontrado = buscarChamado.get();

        // Verifica se o chamado já está cancelado, devolvido, ou encerrado
        if (chamadoEncontrado.getStatusEnum() == StatusEnum.ENCERRADO || chamadoEncontrado.getStatusEnum() == StatusEnum.DEVOLVIDO || chamadoEncontrado.getStatusEnum() == StatusEnum.CANCELADO) {
            log.error(String.format("---- Não é possível atualizar o status do chamado com ID %d, pois ele já está encerrado, devolvido ou cancelado ----", chamadoEncontrado.getId()));
            throw new ChamadoStatusUpdateException(String.format("Não é possível atualizar o status do chamado com ID %d, pois ele já está encerrado, devolvido ou cancelado", chamadoEncontrado.getId()));
        }

        // Atualiza o status do chamado para Em Execução
        chamadoEncontrado.setStatusEnum(StatusEnum.CANCELADO);
        chamadosRepository.save(chamadoEncontrado);

        log.info("---- Status do chamado atualizado para cancelado ----");

        // Retorna DTO do chamado atualizado
        return new ChamadosDTO(chamadoEncontrado);
    }

    public ChamadosDTO atualizarChamadoParaStatusDevolvido(Long id) {

        log.info("---- Iniciando processo de atualização de status de chamado para devolvido ----");

        if (id == null || id <= 0) {
            log.error("---- Erro ao atualizar chamado, id informado é inválido ----");
            throw new ObjectnotFoundException("Id Invalido");
        }

        // Verifica se o chamado existe
        Optional<Chamado> buscarChamado = chamadosRepository.findById(id);
        if (!buscarChamado.isPresent()) {
            log.error("---- Chamado não encontrado ----");
            throw new ObjectnotFoundException("Chamado não encontrado");
        }
        Chamado chamadoEncontrado = buscarChamado.get();

        // Verifica se o status atual do chamado é Aberto ou Em Execução
        if (chamadoEncontrado.getStatusEnum() != StatusEnum.ABERTO && chamadoEncontrado.getStatusEnum() != StatusEnum.EXECUÇÃO) {
            log.error(String.format("---- Não é possível atualizar o status do chamado com ID %d, pois ele não está em Aberto ou Em Execução ----", chamadoEncontrado.getId()));
            throw new ChamadoStatusUpdateException(String.format("Não é possível atualizar o status do chamado com ID %d, pois ele não está em Aberto ou Em Execução", chamadoEncontrado.getId()));
        }

        // Atualiza o status do chamado para Devolvido
        chamadoEncontrado.setStatusEnum(StatusEnum.DEVOLVIDO);
        chamadosRepository.save(chamadoEncontrado);

        log.info("---- Status do chamado atualizado para devolvido ----");

        // Retorna DTO do chamado atualizado
        return new ChamadosDTO(chamadoEncontrado);
    }

    public ChamadosDTO atualizarChamadoParaStatusAberto(Long id) {

        log.info("---- Iniciando processo de atualização de status de chamado para Aberto ----");

        if (id == null || id <= 0) {
            log.error("---- Erro ao atualizar chamado, id informado é inválido ----");
            throw new ObjectnotFoundException("Id Invalido");
        }

        // Verifica se o chamado existe
        Optional<Chamado> buscarChamado = chamadosRepository.findById(id);
        if (!buscarChamado.isPresent()) {
            log.error("---- Chamado não encontrado ----");
            throw new ObjectnotFoundException("Chamado não encontrado");
        }
        Chamado chamadoEncontrado = buscarChamado.get();

        // Verifica se o status atual do chamado é Devolvido
        if (chamadoEncontrado.getStatusEnum() != StatusEnum.DEVOLVIDO) {
            log.error(String.format("---- Não é possível atualizar o status do chamado com ID %d, pois ele não está Devolvido ----", chamadoEncontrado.getId()));
            throw new ChamadoStatusUpdateException(String.format("Não é possível atualizar o status do chamado com ID %d, pois ele não está Devolvido", chamadoEncontrado.getId()));
        }

        // Atualiza o status do chamado para Aberto
        chamadoEncontrado.setStatusEnum(StatusEnum.ABERTO);
        chamadosRepository.save(chamadoEncontrado);

        log.info("---- Status do chamado atualizado para Aberto ----");

        // Retorna DTO do chamado atualizado
        return new ChamadosDTO(chamadoEncontrado);
    }

    //ABERTO(0,"ABERTO")**, EXECUÇÃO(1, "EXECUÇÃO")**, ENCERRADO(2, "ENCERRADO")**, CANCELADO(3, "CANCELADO")**, DEVOLVIDO(4, "DEVOLVIDO");

    private void validaDadosInformados(ChamadosDTO chamadosDTO) {



        if (chamadosDTO.getTitulo() == null || chamadosDTO.getTitulo().isEmpty()) {
            log.error("---- Erro na abertura do chamado, campo titulo é obrigatório. ----");
            throw new DataIntegrityViolationException("---- Erro na abertura do chamado, campo titulo é obrigatório. ----");

        }
        if (chamadosDTO.getStatusEnum() == null) {
            log.error("---- Erro na abertura do chamado, campo status é obrigatório. ----");
            throw new DataIntegrityViolationException("---- Erro na abertura do chamado, campo status é obrigatório. ----");

        }
        if (chamadosDTO.getDescricaoChamado() == null || chamadosDTO.getDescricaoChamado().isEmpty() ) {
            log.error("---- Erro na abertura do chamado, campo descrição é obrigatório. ----");
            throw new DataIntegrityViolationException("---- Erro na abertura do chamado, campo descrição é obrigatório. ----");

        }

        if (chamadosDTO.getDataAbertura() == null) {
            log.error("---- Erro na abertura do chamado, campo data abertura é obrigatório. ----");
            throw new DataIntegrityViolationException("---- Erro na abertura do  chamado campo data abertura é obrigatório. ----");

        }

        if (chamadosDTO.getPrioridade() == null ) {
            log.error("---- Erro na abertura do chamado, campo prioridade é obrigatório. ----");
            throw new DataIntegrityViolationException("---- Erro na abertura do  chamado, campo prioridade é obrigatório. ----");
        }


    }
}