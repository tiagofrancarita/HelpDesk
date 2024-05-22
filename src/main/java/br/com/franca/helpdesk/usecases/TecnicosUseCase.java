package br.com.franca.helpdesk.usecases;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.dtos.TecnicoDTO;
import br.com.franca.helpdesk.domains.enums.PerfilEnum;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.exceptions.TecnicosNotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.ValidationException;
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
            throw new TecnicosNotFoundException("Nenhum técnico encontrado.");
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
            throw new EntityNotFoundException("Técnico não encontrado");
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
            throw new ValidationException("Email já está em uso");
        }

        // Validação dos campos obrigatórios
        if (tecnicoDTO.getNome() == null || tecnicoDTO.getNome().isEmpty()) {
            log.error("---- Erro ao cadastrar o técnico, campo nome é obrigatório. ----");
            throw new ValidationException("Nome é obrigatório");
        }
        if (tecnicoDTO.getSenha() == null || tecnicoDTO.getSenha().isEmpty()) {
            log.error("---- Erro ao cadastrar o técnico, campo senha é obrigatório. ----");
            throw new ValidationException("Senha é obrigatória");
        }
        if (tecnicoDTO.getDataCriacao() == null) {
            log.error("---- Erro ao cadastrar o técnico, campo data de criação é obrigatório. ----");
            throw new ValidationException("Data de criação é obrigatória");
        }
        if (tecnicoDTO.getPerfis() == null ||tecnicoDTO.getPerfis().isEmpty()){
            log.error("---- Erro ao cadastrar o técnico, campo perfil é obrigatório. ----");
            throw new ValidationException("Perfil é obrigatório");
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

        // Cria uma entidade Tecnico a partir do DTO
        Tecnico tecnico = new Tecnico(null, tecnicoDTO.getNome(), tecnicoDTO.getCpf(), tecnicoDTO.getEmail(), tecnicoDTO.getSenha());
        Tecnico finalTecnico = tecnico;
        tecnicoDTO.getPerfis().forEach(perfil -> finalTecnico.addPerfil(PerfilEnum.toEnum(perfil)));

        // Salva o técnico no banco de dados
        tecnico = tecnicoRepository.save(tecnico);
        log.info("---- Técnico salvo com sucesso ----");
        return new TecnicoDTO(tecnico);


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
            throw new IllegalArgumentException("id informado é inválido");
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
                throw new ValidationException(mensagem);
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
            throw new EntityNotFoundException("Técnico não encontrado");
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

        log.info("---- Iniciando validação dos dados informados ----");
        // Validação dos campos obrigatórios
        if (tecnicoAtualizadoDTO.getNome() == null || tecnicoAtualizadoDTO.getNome().isEmpty()) {
            log.error("---- Erro ao atualizar técnico, campo nome é obrigatório. ----");
            throw new ValidationException("O Campo nome é obrigatório");
        }
        if (tecnicoAtualizadoDTO.getEmail() == null || tecnicoAtualizadoDTO.getEmail().isEmpty()) {
            log.error("---- Erro ao atualizar técnico, campo e-mail é obrigatório. ----");
            throw new ValidationException("O Campo e-mail é obrigatório");
        }
        if (tecnicoAtualizadoDTO.getSenha() == null || tecnicoAtualizadoDTO.getSenha().isEmpty()) {
            log.error("---- Erro ao atualizar técnico, campo senha é obrigatório. ----");
            throw new ValidationException("O Campo senha é obrigatório");
        }
        // Você pode adicionar mais validações conforme necessário

        // Atualiza os campos do técnico existente com os valores do DTO
        tecnicoExistente.setNome(tecnicoAtualizadoDTO.getNome());
        tecnicoExistente.setCpf(tecnicoAtualizadoDTO.getCpf());
        tecnicoExistente.setEmail(tecnicoAtualizadoDTO.getEmail());
        tecnicoExistente.setSenha(tecnicoAtualizadoDTO.getSenha());
        // Atualize outros campos conforme necessário

        // Salva o técnico atualizado no banco de dados
        Tecnico tecnicoAtualizado = tecnicoRepository.save(tecnicoExistente);

        log.info("---- Técnico atualizado com sucesso ----");
        return new TecnicoDTO(tecnicoAtualizado);
    }
}