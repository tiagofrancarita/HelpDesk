package br.com.franca.helpdesk.usecases;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Cliente;
import br.com.franca.helpdesk.domains.Pessoa;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.dtos.ClienteDTO;
import br.com.franca.helpdesk.domains.dtos.TecnicoDTO;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.exceptions.DataIntegrityViolationException;
import br.com.franca.helpdesk.exceptions.ObjectnotFoundException;
import br.com.franca.helpdesk.exceptions.TecnicoAndChamadosNotDeleted;
import br.com.franca.helpdesk.exceptions.ValidationException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.ClienteRepository;
import br.com.franca.helpdesk.repositorys.PessoaRepository;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ClienteUseCase {

    private Logger log = LoggerFactory.getLogger(TecnicosUseCase.class);


    private final ClienteRepository clienteRepository;
    private final ChamadosRepository chamadosRepository;
    private final PessoaRepository pessoaRepository;


    @Autowired
    public ClienteUseCase(ClienteRepository clienteRepository, ChamadosRepository chamadosRepository, PessoaRepository pessoaRepository) {
        this.clienteRepository = clienteRepository;
        this.chamadosRepository = chamadosRepository;
        this.pessoaRepository = pessoaRepository;
    }

    public List<ClienteDTO> listarCliente() {
        log.info("---- Iniciando a listagem de técnicos cadastrados.... ----");
        List<Cliente> clientes = clienteRepository.findAll();
        if (clientes.isEmpty()) {
            log.error("Nenhum cliente encontrado");
            throw new ObjectnotFoundException("Nenhum cliente encontrado");
        }
        List<ClienteDTO> clienteDto = clientes.stream()
                .map(ClienteDTO::new)
                .collect(Collectors.toList());
        log.info("---- Técnicos listados com sucesso. ----");
        return clienteDto;
    }

    public ResponseEntity<ClienteDTO> buscarPorId(Long id) {

        log.error("---- Iniciando a busca de cliente por ID.... ----");
        if (id <= 0) {
            throw new IllegalArgumentException("O id informado é invalido");
        }

        Optional<Cliente> buscarClienteID = clienteRepository.findById(id);

        if (buscarClienteID.isEmpty()) {
            log.error("---- Cliente não encontrado. ----ID: " + id);
            throw new ObjectnotFoundException("Nenhum técnico encontrado");
        }

        Cliente clienteEncontrado = buscarClienteID.get();
        log.info("---- Cliente encontrado. ----ID: " + clienteEncontrado.getId());
        return ResponseEntity.ok(new ClienteDTO(clienteEncontrado));

    }

    public Cliente cadastrarCliente(ClienteDTO clienteDTO) {

        log.info("---- Iniciando o cadastro de cliente... ----");

        try {
            log.info("---- Iniciando validação dos dados informados... ----");
            validaDadosInformados(clienteDTO);
            log.info("---- Dados validados com sucesso. ----");

            Cliente clienteSalvo = new Cliente(clienteDTO);

            log.info("---- Cliente salvo com sucesso ----");

            return clienteRepository.save(clienteSalvo);

        }catch (Exception e) {
            log.error("---- Erro ao cadastrar o cliente. ----");
            throw new DataIntegrityViolationException("Erro ao cadastrar o cliente");
        }
    }

    public ClienteDTO deletarClientePorID(Long id) {

        log.info("---- Iniciando processo de deleção de cliente por id ----");

        if (id == null || id <= 0) {
            log.error("---- Erro ao deletar cliente, id informado é inválido ----");
            throw new ObjectNotFoundException("Id Invalido","/listarTecnicos");
        }

        log.info("---- Verificando se há chamados abertos atribuídos ao cliente a ser excluído ----");

        Optional<Cliente> buscarCliente = clienteRepository.findById(id);
        if (buscarCliente.isPresent()) {
            Cliente clienteEncontrado = buscarCliente.get();

            // Verifica se há chamados abertos atribuídos a este técnico
            List<Chamado> chamadosAbertos = chamadosRepository.findByClienteAndStatusEnum(clienteEncontrado, StatusEnum.ABERTO);
            if (!chamadosAbertos.isEmpty()) {
                Chamado chamadoAberto = chamadosAbertos.get(0); // pegar o primeiro chamado em aberto
                String mensagem = String.format("Não é possível excluir o cliente com ID %d. Existem chamados abertos atribuídos a ele. Chamado ID: %d", clienteEncontrado.getId(), chamadoAberto.getId());
                log.error("---- Erro ao deletar cliente, " + mensagem + " ----");
                throw new TecnicoAndChamadosNotDeleted("Não é possível excluir o tecnico informado, o mesmo possui chamados em aberto atrelado a ele.");
            }
            log.info(" ---- Não há chamados abertos, associados ao cliente ---- " + " ID_Tecnico: " + clienteEncontrado.getId());

            // Se não houver chamados abertos, exclui o técnico
            log.info("---- Excluindo Cliente ----");
            clienteRepository.delete(clienteEncontrado);
            log.info("---- Cliente excluído ----");

            // Retorna DTO do técnico excluído
            return new ClienteDTO(clienteEncontrado);

        } else {
            log.error("---- cliente não encontrado ----");
            throw new ObjectNotFoundException("Nenhum cliente encontrado","/listarCliente");
        }
    }

    public ClienteDTO atualizarClientePorId(Long id, ClienteDTO clienteAtualizadoDTO) {

        log.info("---- Iniciando processo de atualização de cliente por ID ----");
        if (id == null || id <= 0) {
            log.error("---- Erro ao atualizar cliente, ID inválido ----");
            throw new IllegalArgumentException("O ID informado é inválido");
        }

        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("---- Erro ao atualizar cliente, cliente não encontrado ---- ID_TECNICO: " + id);
                    return new ObjectnotFoundException("Cliente não encontrado");
                });

        log.info("---- Cliente encontrado, iniciando a atualização. ----" + "ID_CLIENTE: " + clienteExistente.getId());

        clienteExistente.setNome(clienteAtualizadoDTO.getNome());
        clienteExistente.setCpf(clienteAtualizadoDTO.getCpf());
        clienteExistente.setEmail(clienteAtualizadoDTO.getEmail());
        clienteExistente.setSenha(clienteAtualizadoDTO.getSenha());

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        log.info("---- Cliente atualizado com sucesso -----" + "ID_Cliente: " + clienteAtualizado.getId());

        return new ClienteDTO(clienteAtualizado);
    }
    private void validaDadosInformados(ClienteDTO clienteDTO) {

        Optional<Pessoa> pessoaCpf = pessoaRepository.findByCpf(clienteDTO.getCpf());
        if (pessoaCpf.isPresent() && pessoaCpf.get().getId() != clienteDTO.getId()) {
            log.error("---- Erro ao cadastrar o cliente, O Cpf informado já está sendo utilizado. ----");
            throw new DataIntegrityViolationException("O Cpf informado já está sendo utilizado");
        }

        Optional<Pessoa> pessoaEmail = pessoaRepository.findByEmail(clienteDTO.getEmail());
        if (pessoaEmail.isPresent() && pessoaEmail.get().getId() != clienteDTO.getId()) {
            log.error("---- Erro ao cadastrar o cliente, O Email informado já está sendo utilizado. ----");
            throw new DataIntegrityViolationException("O Email informado já está sendo utilizado");
        }

        if (clienteDTO.getNome() == null || clienteDTO.getNome().isEmpty()) {
            log.error("---- Erro ao cadastrar o cliente, campo nome é obrigatório. ----");
            throw new DataIntegrityViolationException("O campo nome é obrigatório");

        }
        if (clienteDTO.getSenha() == null || clienteDTO.getSenha().isEmpty()) {
            log.error("---- Erro ao cadastrar o cliente, campo senha é obrigatório. ----");
            throw new DataIntegrityViolationException("O campo senha é obrigatório");

        }
        if (clienteDTO.getDataCriacao() == null) {
            log.error("---- Erro ao cadastrar o cliente, campo data de criação é obrigatório. ----");
            throw new DataIntegrityViolationException("O campo data criação é obrigatório");
        }
        if (clienteDTO.getPerfis() == null || clienteDTO.getPerfis().isEmpty()) {
            log.error("---- Erro ao cadastrar o cliente, campo perfil é obrigatório. ----");
            throw new DataIntegrityViolationException("O campo perfil é obrigatório");
        }

        if (!isValidPassword(clienteDTO.getSenha())) {
            log.error("---- A senha não atende aos critérios mínimos. ----");
            log.info("---- A senha deve ter pelo menos 8 caracteres.  ----");
            log.info("---- A senha deve conter pelo menos uma letra minúscula  ----");
            log.info("---- A senha deve conter pelo menos uma letra maiúscula ----");
            log.info("---- A senha deve conter pelo menos um dígito (NÚMERO) ----");
            log.info("---- A senha deve conter pelo menos um caractere especial ----");
            log.info("---- A senha não deve conter espaços em branco ----");
            throw new ValidationException("A senha não atende aos critérios mínimos. A Senha deverá conter pelo menos 8 caracteres, uma letra minúscula, uma letra maiúscula, um dígito, um caractere especial e não deve conter espaços em branco.");
        }
    }

    private boolean isValidPassword(String password) {
        // Expressão regular para validar a senha
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(password).matches();
    }
}