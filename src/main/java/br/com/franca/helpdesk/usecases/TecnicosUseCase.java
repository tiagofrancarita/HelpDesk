package br.com.franca.helpdesk.usecases;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.dtos.TecnicoDTO;
import br.com.franca.helpdesk.domains.enums.Perfil;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.exceptions.PerfilNotFoundException;
import br.com.franca.helpdesk.exceptions.TecnicoNotExludeAssociantioTicket;
import br.com.franca.helpdesk.exceptions.TecnicosNotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TecnicosUseCase {

    private Logger log = LoggerFactory.getLogger(TecnicosUseCase.class);
    private final TecnicoRepository tecnicoRepository;
    private final ChamadosRepository chamadosRepository;

    @Autowired
    public TecnicosUseCase(TecnicoRepository tecnicoRepository, ChamadosRepository chamadosRepository) {
        this.tecnicoRepository = tecnicoRepository;
        this.chamadosRepository = chamadosRepository;
    }

    public List<TecnicoDTO> listarTecnicos() {
        log.info("---- Iniciando a listagem de técnicos cadastrados.... ----");
        List<Tecnico> tecnicos = tecnicoRepository.findAll();
        if (tecnicos.isEmpty()) {
            log.error("Nenhum técnico encontrado");
            throw new TecnicosNotFoundException("Nenhum técnico encontrado.", HttpStatus.NOT_FOUND, "Not Found", "/listarTecnicos");
        }
        List<TecnicoDTO> tecnicoDTOs = tecnicos.stream()
                .map(TecnicoDTO::new)
                .collect(Collectors.toList());
        log.info("---- Técnicos listados com sucesso. ----");
        return tecnicoDTOs;
    }


    public ResponseEntity<TecnicoDTO> buscarPorId(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Tecnico> optionalTecnico = tecnicoRepository.findById(id);

        if (optionalTecnico.isEmpty()) {
            log.error("---- Técnico não encontrado. ----ID: " + id);
            throw new TecnicosNotFoundException("Técnico não encontrado.", HttpStatus.NOT_FOUND, "Not Found", "/buscarPorId/" + id);
        }

        Tecnico tecnico = optionalTecnico.get();
        log.info("---- Técnico encontrado. ----ID: " + tecnico.getId());
        return ResponseEntity.ok(new TecnicoDTO(tecnico));
    }


    public TecnicoDTO cadastrarTecnico(@Valid TecnicoDTO tecnicoDTO) {

        log.info("---- Iniciando o cadastro do técnico... ----");

        log.info("---- Iniciando validação dos dados informados... ----");
        // Verifica se já existe um técnico com o mesmo email
        Optional<Tecnico> existingTecnico = tecnicoRepository.findByEmail(tecnicoDTO.getEmail());
        if (existingTecnico.isPresent()) {
            log.error("---- Erro ao cadastrar o técnico, e-mail informado já está sendo utilizado. ----");
            throw new TecnicosNotFoundException("Erro ao cadastrar o técnico, e-mail informado já está sendo utilizado.", HttpStatus.BAD_REQUEST, "Not Found", "v1/api-helpdesk/tecnicos/cadastrarTecnico");
        }

        Optional<Tecnico> existingCpf = tecnicoRepository.findByCpf(tecnicoDTO.getCpf());
        if (existingCpf.isPresent()) {
            log.error("---- Erro ao cadastrar o técnico, cpf informado já está sendo utilizado. ----");
            throw new TecnicosNotFoundException("Erro ao cadastrar o técnico, cpf informado já está sendo utilizado.", HttpStatus.BAD_REQUEST, "Not Found", "v1/api-helpdesk/tecnicos/cadastrarTecnico");
        }

        // Validação dos campos obrigatórios
        if (tecnicoDTO.getNome() == null || tecnicoDTO.getNome().isEmpty()) {
            log.error("---- Erro ao cadastrar o técnico, campo nome é obrigatório. ----");
            throw new TecnicosNotFoundException("Erro ao cadastrar o técnico, campo nome é obrigatório.", HttpStatus.BAD_REQUEST, "Not Found", "v1/api-helpdesk/tecnicos/cadastrarTecnico");
        }
        if (tecnicoDTO.getSenha() == null || tecnicoDTO.getSenha().isEmpty()) {
            log.error("---- Erro ao cadastrar o técnico, campo senha é obrigatório. ----");
            throw new TecnicosNotFoundException("Erro ao cadastrar o técnico, campo senha é obrigatório.", HttpStatus.BAD_REQUEST, "Not Found", "v1/api-helpdesk/tecnicos/cadastrarTecnico");
        }
        if (tecnicoDTO.getDataCriacao() == null) {
            log.error("---- Erro ao cadastrar o técnico, campo data de criação é obrigatório. ----");
            throw new TecnicosNotFoundException("Erro ao cadastrar o técnico, campo senha é obrigatório.", HttpStatus.BAD_REQUEST, "Not Found", "v1/api-helpdesk/tecnicos/cadastrarTecnico");
        }
        if (tecnicoDTO.getPerfis() == null || tecnicoDTO.getPerfis().isEmpty()) {
            log.error("---- Erro ao cadastrar o técnico, campo perfil é obrigatório. ----");
            throw new TecnicosNotFoundException("Erro ao cadastrar o técnico, campo senha é obrigatório.", HttpStatus.BAD_REQUEST, "Not Found", "v1/api-helpdesk/tecnicos/cadastrarTecnico");
        }


        // Validação da senha
        if (!isValidPassword(tecnicoDTO.getSenha())) {
            log.error("---- A senha não atende aos critérios mínimos. ----");
            log.info("---- A senha deve ter pelo menos 8 caracteres.  ----");
            log.info("---- A senha deve conter pelo menos uma letra minúscula  ----");
            log.info("---- A senha deve conter pelo menos uma letra maiúscula ----");
            log.info("---- A senha deve conter pelo menos um dígito (NÚMERO) ----");
            log.info("---- A senha deve conter pelo menos um caractere especial ----");
            log.info("---- A senha não deve conter espaços em branco ----");
            throw new ValidationException("A senha não atende aos critérios mínimos");
        }


        Tecnico tecnicoSalvo = new Tecnico(tecnicoDTO);

        // Salva o técnico no banco de dados

        log.info("---- Técnico salvo com sucesso ----");
        return new TecnicoDTO(tecnicoRepository.save(tecnicoSalvo));

    }

    private boolean isValidPassword(String password) {
        // Expressão regular para validar a senha
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(password).matches();
    }

    public TecnicoDTO deletarTecnico(Long id) {

        log.info("---- Iniciando processo de deleção de técnico por id ----");

        if (id == null || id <= 0) {
            log.error("---- Erro ao deletar técnico, id informado é inválido ----");
            throw new TecnicoNotExludeAssociantioTicket("ID inválido", HttpStatus.BAD_REQUEST, "Bad Request", "/deletarTecnico/" + id);
        }

        log.info("---- Verificando se há chamados abertos atribuídos ao técnico a ser excluído ----");

        Optional<Tecnico> optionalTecnico = tecnicoRepository.findById(id);
        if (optionalTecnico.isPresent()) {
            Tecnico tecnico = optionalTecnico.get();

            // Verifica se há chamados abertos atribuídos a este técnico
            List<Chamado> chamadosAbertos = chamadosRepository.findByTecnicoAndStatusEnum(tecnico, StatusEnum.ABERTO);
            if (!chamadosAbertos.isEmpty()) {
                Chamado chamadoAberto = chamadosAbertos.get(0); // pegar o primeiro chamado em aberto
                String mensagem = String.format("Não é possível excluir o técnico com ID %d. Existem chamados abertos atribuídos a ele. Chamado ID: %d", tecnico.getId(), chamadoAberto.getId());
                log.error("---- Erro ao deletar técnico, " + mensagem + " ----");
                throw new TecnicoNotExludeAssociantioTicket("Não é possível excluir o técnico. Existem chamados abertos atribuídos", HttpStatus.BAD_REQUEST, "Bad Request", "/deletarTecnico/" + id);
            }
            log.info(" ---- Não há chamados abertos, associados ao técnico ---- " + " ID_Tecnico: " + tecnico.getId());

            // Se não houver chamados abertos, exclui o técnico
            log.info("---- Excluindo técnico ----");
            tecnicoRepository.delete(tecnico);
            log.info("---- Técnico excluído ----");

            // Retorna DTO do técnico excluído
            return new TecnicoDTO(tecnico);

        } else {
            log.error("---- Técnico não encontrado ----");
            throw new TecnicosNotFoundException("Técnico não encontrado", HttpStatus.NOT_FOUND, "Not Found", "/deletarTecnico/" + id);
        }
    }

    public TecnicoDTO atualizarTecnico(Long id, TecnicoDTO tecnicoAtualizadoDTO) {
        log.info("---- Iniciando processo de atualização de técnico por ID ----");

        // Verifica se o ID é válido
        if (id == null || id <= 0) {
            log.error("---- Erro ao atualizar técnico, ID inválido ----");
            throw new IllegalArgumentException("O ID informado é inválido");
        }

        // Busca o técnico existente pelo ID
        Tecnico tecnicoExistente = tecnicoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("---- Erro ao atualizar técnico, técnico não encontrado ---- ID_TECNICO: " + id);
                    return new EntityNotFoundException("Técnico não encontrado");
                });

        log.info("---- Tecnico encontrado, iniciando a atualização. ----" + "ID_TECNICO: " + tecnicoExistente.getId());


        // Salvar o técnico atualizado no repositório
        Tecnico tecnicoAtualizado = tecnicoRepository.save(tecnicoExistente);
        log.info("---- Tecnico atualizado com sucesso -----" + "ID_TECNICO: " + tecnicoAtualizado.getId());


        return new TecnicoDTO(tecnicoAtualizado);
    }
}