package br.com.franca.helpdesk.usecases;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.OrdemServico;
import br.com.franca.helpdesk.domains.dtos.ChamadosDTO;
import br.com.franca.helpdesk.exceptions.DataIntegrityViolationException;
import br.com.franca.helpdesk.exceptions.ObjectnotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.ClienteRepository;
import br.com.franca.helpdesk.repositorys.OrdemServicoRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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

    public ResponseEntity<OrdemServico> buscarOsPorId(Long id) {

        log.error("---- Iniciando a busca de ordem serviço por ID.... ----");


        if (id <= 0) {
            throw new IllegalArgumentException("O id informado é invalido");
        }

        Optional<OrdemServico> buscaOs = ordemServicoRepository.findById(id);

        if (buscaOs.isEmpty()) {
            log.error("---- Os não encontrada. ----ID: " + id);
            throw new ObjectnotFoundException("Nenhum chamado encontrado");
        }

        OrdemServico OsEncontrada = buscaOs.get();
        log.info("---- Ordem de serviço encontrada. ----ID: " + OsEncontrada.getId());
        return ResponseEntity.ok().body(OsEncontrada);
    }

    public List<OrdemServico> listarOs() {

        log.info("---- Iniciando a listagem de ordem de serviços cadastrados.... ----");

        List<OrdemServico> buscarOrdemSerico = ordemServicoRepository.findAll();
        if (buscarOrdemSerico.isEmpty()) {
            log.error("Nenhuma os encontrada");
            throw new ObjectnotFoundException("Nenhuma os encontrada");
        }
        List<OrdemServico> listagemOS = buscarOrdemSerico.stream().collect(Collectors.toList());
        log.info("---- Listagem de ordem de serviços realizada com sucesso. ----");
        return listagemOS;
    }

    @Transactional
    public OrdemServico gerarOrdemServico(Long chamadoId) {

        log.info("---- Iniciando a geração de ordem de serviço.... ----");

        Optional<Chamado> chamadoOpt = chamadosRepository.findById(chamadoId);
        if (!chamadoOpt.isPresent()) {
            log.error("---- Chamado não encontrado ----");
            throw new RuntimeException("Chamado não encontrado");

        }

        Chamado chamado = chamadoOpt.get();
        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setChamado(chamado);
        ordemServico.setDescricao(chamado.getDescricaoChamado());
        ordemServico.setDataCriacao(LocalDateTime.now());
        String numeroChamado = gerarNumeroChamado();
        System.out.println("Numero chamado: " + numeroChamado);
        ordemServico.setNumeroChamado(gerarNumeroChamado());
        log.info("---- Salvando ordem serivço.... ----");

        log.info("---- Ordem serivço salva com sucesso. ----");
        return ordemServicoRepository.save(ordemServico);
    }

    public String gerarNumeroChamado() {
        Random random = new Random();
        int numero = random.nextInt(1000000);
        return String.format("CHG-%06d", numero);
    }
}