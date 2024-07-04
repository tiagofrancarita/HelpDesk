package br.com.franca.helpdesk.usecases;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Pessoa;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.dtos.TecnicoDTO;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.exceptions.DataIntegrityViolationException;
import br.com.franca.helpdesk.exceptions.ObjectnotFoundException;
import br.com.franca.helpdesk.exceptions.TecnicoAndChamadosNotDeleted;
import br.com.franca.helpdesk.exceptions.ValidationException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.PessoaRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TecnicosUseCase {

    private Logger log = LoggerFactory.getLogger(TecnicosUseCase.class);
    private final TecnicoRepository tecnicoRepository;
    private final ChamadosRepository chamadosRepository;
    private final PessoaRepository pessoaRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public TecnicosUseCase(TecnicoRepository tecnicoRepository, ChamadosRepository chamadosRepository, PessoaRepository pessoaRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.tecnicoRepository = tecnicoRepository;
        this.chamadosRepository = chamadosRepository;
        this.pessoaRepository = pessoaRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<TecnicoDTO> listarTecnicos() {
        log.info("---- Iniciando a listagem de técnicos cadastrados.... ----");
        List<Tecnico> tecnicos = tecnicoRepository.findAll();
        if (tecnicos.isEmpty()) {
            log.error("Nenhum técnico encontrado");
            throw new ObjectnotFoundException("Nenhum técnico encontrado");
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
            throw new ObjectnotFoundException("Nenhum técnico encontrado");
        }

        Tecnico tecnico = optionalTecnico.get();
        log.info("---- Técnico encontrado. ----ID: " + tecnico.getId());
        return ResponseEntity.ok(new TecnicoDTO(tecnico));
    }


    public Tecnico cadastrarTecnico(TecnicoDTO tecnicoDTO) {

        log.info("---- Iniciando o cadastro do técnico... ----");

        log.info("---- Iniciando validação dos dados informados... ----");
        validaCpfAndEmail(tecnicoDTO);


        // Validação dos campos obrigatórios
        if (tecnicoDTO.getNome() == null || tecnicoDTO.getNome().isEmpty()) {
            log.error("---- Erro ao cadastrar o técnico, campo nome é obrigatório. ----");
            throw new DataIntegrityViolationException("O campo nome é obrigatório");


        }
        if (tecnicoDTO.getSenha() == null || tecnicoDTO.getSenha().isEmpty()) {
            log.error("---- Erro ao cadastrar o técnico, campo senha é obrigatório. ----");
            throw new DataIntegrityViolationException("O campo senha é obrigatório");

        }
        if (tecnicoDTO.getDataCriacao() == null) {
            log.error("---- Erro ao cadastrar o técnico, campo data de criação é obrigatório. ----");
            throw new DataIntegrityViolationException("O campo data criação é obrigatório");

        }
        if (tecnicoDTO.getPerfis() == null || tecnicoDTO.getPerfis().isEmpty()) {
            log.error("---- Erro ao cadastrar o técnico, campo perfil é obrigatório. ----");
            throw new DataIntegrityViolationException("O campo perfil é obrigatório");

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
            throw new ValidationException("A senha não atende aos critérios mínimos. A Senha deverá conter pelo menos 8 caracteres, uma letra minúscula, uma letra maiúscula, um dígito, um caractere especial e não deve conter espaços em branco.");
        }

        // Criptografa a senha
        log.info("---- Criptografando a senha informada aguarde.... ----");
        tecnicoDTO.setSenha(bCryptPasswordEncoder.encode(tecnicoDTO.getSenha()));
        log.info("---- Senha criptografada com sucesso. ----");

        log.info("---- Salvando dados informados... ----");

        Tecnico tecnicoSalvo = new Tecnico(tecnicoDTO);

        log.info("---- Técnico salvo com sucesso ----");
        return tecnicoRepository.save(tecnicoSalvo);

    }

    public TecnicoDTO deletarTecnico(Long id) {

        log.info("---- Iniciando processo de deleção de técnico por id ----");

        if (id == null || id <= 0) {
            log.error("---- Erro ao deletar técnico, id informado é inválido ----");
            throw new ObjectNotFoundException("Id Invalido","/listarTecnicos");
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
                throw new TecnicoAndChamadosNotDeleted("Não é possível excluir o tecnico informado, o mesmo possui chamados em aberto atrelado a ele.");
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
            throw new ObjectnotFoundException("Nenhum técnico encontrado");
        }
    }

    public TecnicoDTO atualizarTecnico(Long id, TecnicoDTO tecnicoAtualizadoDTO) {
        log.info("---- Iniciando processo de atualização de técnico por ID ----");


        log.info("---- Verificando se o id informado é um id valido ----");
        if (id == null || id <= 0) {
            log.error("---- Erro ao atualizar técnico, ID inválido ----");
            throw new IllegalArgumentException("O ID informado é inválido");
        }


        log.info("---- Verificando se o id informado existe no banco de dados. ----");
        Tecnico tecnicoExistente = tecnicoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("---- Erro ao atualizar técnico, técnico não encontrado ---- ID_TECNICO: " + id);
                    return new ObjectnotFoundException("Técnico não encontrado");
                });

        log.info("---- Verificando se a senha foi alterada ----");
        if (tecnicoAtualizadoDTO.getSenha() != null && !tecnicoAtualizadoDTO.getSenha().equals(tecnicoExistente.getSenha())) {
            log.info("---- Validando a senha foi alterada ----");
            String novaSenha = tecnicoAtualizadoDTO.getSenha();
            if (!novaSenha.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                log.error("---- Erro ao atualizar técnico, senha inválida ----");
                throw new IllegalArgumentException("A nova senha não atende aos requisitos");
            }

            log.info("---- Criptografando a nova senha ----");
            tecnicoAtualizadoDTO.setSenha(bCryptPasswordEncoder.encode(novaSenha));
        } else {
            log.info("---- Senha não alterada ----");
            tecnicoAtualizadoDTO.setSenha(tecnicoExistente.getSenha());
        }

        log.info("---- Atualizando os outros campos. ----");
        tecnicoExistente.setNome(tecnicoAtualizadoDTO.getNome());
        tecnicoExistente.setCpf(tecnicoAtualizadoDTO.getCpf());
        tecnicoExistente.setEmail(tecnicoAtualizadoDTO.getEmail());
        tecnicoExistente.setDataCriacao(tecnicoAtualizadoDTO.getDataCriacao());

        log.info("---- Salvando as alterações, aguarde.. ----");
        tecnicoRepository.save(tecnicoExistente);
        log.info("---- Alterações salvas com sucesso. ----");
        // Converte o técnico atualizado para DTO antes de retornar
        return new TecnicoDTO(tecnicoExistente);
    }

    private void validaCpfAndEmail(TecnicoDTO tecnicoDTO) {

        Optional<Pessoa> pessoaCpf = pessoaRepository.findByCpf(tecnicoDTO.getCpf());
            if (pessoaCpf.isPresent() && pessoaCpf.get().getId() != tecnicoDTO.getId()) {
                log.error("---- Erro ao cadastrar o técnico, O Cpf informado já está sendo utilizado. ----");
                throw new DataIntegrityViolationException("O Cpf informado já está sendo utilizado");
            }

        Optional<Pessoa> pessoaEmail = pessoaRepository.findByEmail(tecnicoDTO.getEmail());
            if (pessoaEmail.isPresent() && pessoaEmail.get().getId() != tecnicoDTO.getId()) {
                log.error("---- Erro ao cadastrar o técnico, O Email informado já está sendo utilizado. ----");
                throw new DataIntegrityViolationException("O Email informado já está sendo utilizado");
            }
    }

    private boolean isValidPassword(String password) {
        // Expressão regular para validar a senha
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(password).matches();
    }
}

/*
        // Validação da senha
        if (!isValidPassword(tecnicoDTO.getSenha())) {
            log.error("---- A senha não atende aos critérios mínimos. ----");
            log.info("---- A senha deve ter pelo menos 8 caracteres.  ----");
            log.info("---- A senha deve conter pelo menos uma letra minúscula  ----");
            log.info("---- A senha deve conter pelo menos uma letra maiúscula ----");
            log.info("---- A senha deve conter pelo menos um dígito (NÚMERO) ----");
            log.info("---- A senha deve conter pelo menos um caractere especial ----");
            log.info("---- A senha não deve conter espaços em branco ----");
            throw new ValidationException("A senha não atende aos critérios mínimos. A Senha deverá conter pelo menos 8 caracteres, uma letra minúscula, uma letra maiúscula, um dígito, um caractere especial e não deve conter espaços em branco.");
        }
 */