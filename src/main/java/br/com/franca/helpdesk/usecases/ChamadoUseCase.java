package br.com.franca.helpdesk.usecases;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Cliente;
import br.com.franca.helpdesk.domains.Pessoa;
import br.com.franca.helpdesk.domains.dtos.ChamadosDTO;
import br.com.franca.helpdesk.domains.dtos.ClienteDTO;
import br.com.franca.helpdesk.exceptions.DataIntegrityViolationException;
import br.com.franca.helpdesk.exceptions.ObjectnotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
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

    private Logger log = LoggerFactory.getLogger(TecnicosUseCase.class);


    @Autowired
    private ChamadosRepository chamadosRepository;
    @Autowired
    private TecnicosUseCase tecnicosUseCase;
    @Autowired
    private ClienteUseCase clienteUseCase;

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
