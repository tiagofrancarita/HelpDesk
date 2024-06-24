package br.com.franca.helpdesk.usecases;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.OrdemServico;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.exceptions.ChamadoStatusInvalidoException;
import br.com.franca.helpdesk.exceptions.ObjectnotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.ClienteRepository;
import br.com.franca.helpdesk.repositorys.OrdemServicoRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrdemServicoUseCase {

    private Logger log = LoggerFactory.getLogger(ChamadoUseCase.class);
    private final ChamadosRepository chamadosRepository;
    private final TecnicosUseCase tecnicosUseCase;
    private final ClienteUseCase clienteUseCase;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final EmailUseCase emailUseCase;
    private final OrdemServicoRepository ordemServicoRepository;

    @Autowired
    public OrdemServicoUseCase(ChamadosRepository chamadosRepository, TecnicosUseCase tecnicosUseCase, ClienteUseCase clienteUseCase, ClienteRepository clienteRepository, TecnicoRepository tecnicoRepository, EmailUseCase emailUseCase, OrdemServicoRepository ordemServicoRepository) {
        this.chamadosRepository = chamadosRepository;
        this.tecnicosUseCase = tecnicosUseCase;
        this.clienteUseCase = clienteUseCase;
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.emailUseCase = emailUseCase;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    public OrdemServico buscarOrdemServicoPorId(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de Serviço não encontrada"));
    }

    public List<OrdemServico> listarOs() {

        log.info("---- Iniciando a listagem de ordem de serviços cadastrados.... ----");

        List<OrdemServico> buscarOrdemSerico = ordemServicoRepository.findAll();
        if (buscarOrdemSerico.isEmpty()) {
            log.error("Nenhuma ordem de serviço encontrada");
            throw new ObjectnotFoundException("Nenhuma ordem de serviço encontrada");
        }
        List<OrdemServico> listagemOS = buscarOrdemSerico.stream().collect(Collectors.toList());
        log.info("---- Listagem de ordem de serviços realizada com sucesso. ----");
        return listagemOS;
    }

    public OrdemServico criarOrdemServico(Long chamadoId, String descricao, String problema, String tratativa, String solucao) {
        // Buscar o chamado pelo ID
        Chamado chamado = chamadosRepository.findById(chamadoId)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));

        // Verificar se o chamado está com status ABERTO
        if (!chamado.getStatusEnum().equals(StatusEnum.ABERTO)) {
            throw new ChamadoStatusInvalidoException("A Ordem de Serviço só pode ser criada para chamados com status ABERTO");
        }

        // Criar a nova ordem de serviço
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setChamado(chamado);
        ordemServico.setNumeroChamado(generateNumeroChamado());
        ordemServico.setDescricao(descricao);
        ordemServico.setDataCriacao(LocalDateTime.now());
        ordemServico.setProblema(problema);
        ordemServico.setTratativa(tratativa);
        ordemServico.setSolucao(solucao);
        ordemServico.setStatusEnum(StatusEnum.ABERTO);

        // Salvar a ordem de serviço no banco de dados
        return ordemServicoRepository.save(ordemServico);
    }

    public OrdemServico atualizarStatusParaExecucao(Long id) {
        OrdemServico ordemServico = buscarOrdemServicoPorId(id);
        if (ordemServico.getStatusEnum() == StatusEnum.CANCELADO || ordemServico.getStatusEnum() == StatusEnum.ENCERRADO) {
            throw new ChamadoStatusInvalidoException("Não é possível alterar o status para EXECUCAO. A OS está CANCELADA ou ENCERRADA.");
        }
        if (ordemServico.getStatusEnum() == StatusEnum.ABERTO || ordemServico.getStatusEnum() == StatusEnum.DEVOLVIDO) {
            ordemServico.setStatusEnum(StatusEnum.EXECUÇÃO);
            ordemServicoRepository.save(ordemServico);
        }
        return ordemServico;
    }

    public OrdemServico atualizarStatusParaDevolvido(Long id) {
        OrdemServico ordemServico = buscarOrdemServicoPorId(id);
        if (ordemServico.getStatusEnum() == StatusEnum.CANCELADO || ordemServico.getStatusEnum() == StatusEnum.ENCERRADO) {
            throw new ChamadoStatusInvalidoException("Não é possível alterar o status para DEVOLVIDO. A OS está CANCELADA ou ENCERRADA.");
        }
        ordemServico.setStatusEnum(StatusEnum.DEVOLVIDO);
        ordemServicoRepository.save(ordemServico);
        return ordemServico;
    }

    public OrdemServico atualizarStatusParaCancelado(Long id) {
        OrdemServico ordemServico = buscarOrdemServicoPorId(id);
        if (ordemServico.getStatusEnum() == StatusEnum.CANCELADO || ordemServico.getStatusEnum() == StatusEnum.ENCERRADO) {
            throw new ChamadoStatusInvalidoException("Não é possível alterar o status para CANCELADO. A OS já está CANCELADA ou ENCERRADA.");
        }
        ordemServico.setStatusEnum(StatusEnum.CANCELADO);
        ordemServicoRepository.save(ordemServico);
        return ordemServico;
    }

    public OrdemServico atualizarStatusParaEncerrado(Long id) {
        OrdemServico ordemServico = buscarOrdemServicoPorId(id);
        if (ordemServico.getStatusEnum() == StatusEnum.CANCELADO || ordemServico.getStatusEnum() == StatusEnum.ENCERRADO) {
            throw new ChamadoStatusInvalidoException("Não é possível alterar o status para ENCERRADO. A OS já está CANCELADA ou ENCERRADA.");
        }
        ordemServico.setStatusEnum(StatusEnum.ENCERRADO);
        ordemServico.setDataFechamento(LocalDateTime.now());
        ordemServicoRepository.save(ordemServico);
        return ordemServico;
    }

    public OrdemServico atualizarStatusParaAberto(Long id) {
        OrdemServico ordemServico = buscarOrdemServicoPorId(id);
        StatusEnum statusAtual = ordemServico.getStatusEnum();

        if (statusAtual == StatusEnum.CANCELADO || statusAtual == StatusEnum.ENCERRADO) {
            throw new ChamadoStatusInvalidoException("Ordem de Serviço com status CANCELADO ou ENCERRADO não pode ser atualizada para EXECUCAO");
        }

        if (statusAtual != StatusEnum.ABERTO && statusAtual != StatusEnum.DEVOLVIDO) {
            throw new ChamadoStatusInvalidoException("Ordem de Serviço só pode ser atualizada para EXECUCAO se o status atual for ABERTO ou DEVOLVIDO");
        }

        ordemServico.setStatusEnum(StatusEnum.ABERTO);
        return ordemServicoRepository.save(ordemServico);
    }

    private String generateNumeroChamado() {
        return UUID.randomUUID().toString();
    }
}