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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final TecnicoRepository tecnicoRepository;
    private final EmailUseCase emailUseCase;

    @Autowired
    public ChamadoUseCase(ChamadosRepository chamadosRepository, TecnicosUseCase tecnicosUseCase, ClienteUseCase clienteUseCase, ClienteRepository clienteRepository, TecnicoRepository tecnicoRepository, EmailUseCase emailUseCase) {
        this.chamadosRepository = chamadosRepository;
        this.tecnicosUseCase = tecnicosUseCase;
        this.clienteUseCase = clienteUseCase;
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.emailUseCase = emailUseCase;
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

            Chamado chamadoSalvo = new Chamado(chamadosDTO);

            log.info("---- Chamado salvo com sucesso ----");

            return chamadosRepository.save(chamadoSalvo);

        } catch (Exception e) {
            log.error("---- Erro ao cadastrar o chamado. ----");
            throw new DataIntegrityViolationException("Erro ao cadastrar o chamado");
        }
    }

    public Chamado atualizaInfoChamado(Long id, @Valid ChamadosDTO objDTO) {

        log.info("---- Iniciando processo de atualização de chamado por id ----");

        // Verifica se o chamado com o ID existe no banco de dados
        log.info("---- Verificando se o chamado com o ID informado existe ----");
        Chamado chamadoExistente = chamadosRepository.findById(id)
                .orElseThrow(() -> new ObjectnotFoundException("Chamado não encontrado com o ID: " + id));

        log.info("---- Chamado encontrado, id: ----" + chamadoExistente.getId());

        log.info("---- Atualizando informações do chamado ----");
        // Atualiza os campos do chamado existente com os dados do DTO
        chamadoExistente.setTituloChamado(objDTO.getTitulo());
        chamadoExistente.setDescricaoChamado(objDTO.getDescricaoChamado());
        chamadoExistente.setStatusEnum(objDTO.getStatusEnum());
        chamadoExistente.setPrioridadeEnum(objDTO.getPrioridade());
        chamadoExistente.setObservacao(objDTO.getObservacoes());
        chamadoExistente.setTecnico(objDTO.getTecnico());
        chamadoExistente.setCliente(objDTO.getCliente());

        log.info("---- Informações atualizadas. ----");

        // Salva e retorna o chamado atualizado
        log.info("---- Salvando informações atualizadas do chamado ----");
        return chamadosRepository.save(chamadoExistente);
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

    @Scheduled(fixedRate = 300000)
    public void chamadosAbertosHa7DiasOuMais (){
        log.info("---- Iniciando verificação de chamados abertos há 7 dias ou mais ----");

        // Calcula a data de 7 dias atrás
        LocalDateTime seteDiasAtras = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

        // Busca todos os chamados com status Aberto há 7 dias ou mais
        List<Chamado> chamadosAbertos = chamadosRepository.findByStatusEnumAndDataAberturaBefore(StatusEnum.ABERTO, seteDiasAtras);

        if (chamadosAbertos.isEmpty()) {
            log.info("---- Nenhum chamado com status Aberto há 7 dias ou mais foi encontrado ----");
        } else {
            // Envia alerta
            enviarAlertaChamadosAbertosHa7DiasOuMais(chamadosAbertos);
        }

    }

    private void enviarAlertaChamadosAbertosHa7DiasOuMais(List<Chamado> chamadosAbertos) {
        log.warn("---- ALERTA: Existem chamados com status Aberto há 7 dias ou mais ----");

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Os seguintes chamados estão abertos há 7 dias ou mais:\n");

        for (Chamado chamado : chamadosAbertos) {
            String infoChamado = String.format("Chamado ID: %d, Data de Abertura: %s, Cliente ID: %d\n",
                    chamado.getId(), chamado.getDataAbertura(), chamado.getCliente().getId());
            mensagem.append(infoChamado);
            log.warn(infoChamado);
        }

        // Envia email
        String destinatario = "tiagofranca.ritaa@outlook.com"; // Altere para o email desejado
        String assunto = "Alerta: Chamados abertos há 7 dias ou mais";
        emailUseCase.sendSimpleMessage(destinatario, assunto, mensagem.toString());
    }


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
