package br.com.franca.helpdesk.controller;

import br.com.franca.helpdesk.domains.Cliente;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.dtos.ClienteDTO;
import br.com.franca.helpdesk.domains.dtos.TecnicoDTO;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.ClienteRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import br.com.franca.helpdesk.usecases.ClienteUseCase;
import br.com.franca.helpdesk.usecases.TecnicosUseCase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "v1/clientes", produces = "application/json")
@Api(value = "Cliente Management System", description = "Operações referentes ao cliente no Sistema de Helpdesk")
public class ClienteController {

    private Logger log = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteRepository clienteRepository;
    private final ClienteUseCase clienteUseCase;
    private final ChamadosRepository chamadosRepository;

    @Autowired
    public ClienteController(ClienteRepository clienteRepository, ClienteUseCase clienteUseCase, ChamadosRepository chamadosRepository) {
        this.clienteRepository = clienteRepository;
        this.clienteUseCase = clienteUseCase;
        this.chamadosRepository = chamadosRepository;
    }

    @GetMapping("/listarClientes")
    @ApiOperation(value = "Lista todos os clientes cadastrados", response = ClienteDTO.class, responseContainer = "List", produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Clientes listados com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum cliente encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<List<ClienteDTO>> listarClientes() {

        List<ClienteDTO> listagemClientesCadastrados = clienteUseCase.listarCliente();
        return new ResponseEntity<>(listagemClientesCadastrados, HttpStatus.OK);

    }

    @ApiOperation(value = "Busca um cliente cadastrado pelo id", response = ClienteDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "cliente listados com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum cliente encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @GetMapping(value = "buscarClientePorId/{id}")
    public ResponseEntity<?> buscarClientePorId(@PathVariable Long id) {

        ClienteDTO buscarClientePorId = clienteUseCase.buscarPorId(id).getBody();
        return ResponseEntity.ok(buscarClientePorId);

    }


    @ApiOperation(value = "Cadastra um novo cliente", response = ClienteDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "cliente criado com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao criar cliente"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @PostMapping("/cadastrarCliente")
    public ResponseEntity<ClienteDTO> cadastrarCliente(@Valid @RequestBody ClienteDTO clienteDTO) {

        Cliente novoCliente = clienteUseCase.cadastrarCliente(clienteDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(novoCliente.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @ApiOperation(value = "Deleta um cliente por id", response = ClienteDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "cliente excluido com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao excluir cliente"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @DeleteMapping("deletaClientePorId/{id}")
    public ResponseEntity<String> deletaClientePorId(@PathVariable Long id) {

        clienteUseCase.deletarClientePorID(id);
        return new ResponseEntity<String>("Cliente excluído com sucesso", HttpStatus.OK);

    }

    @ApiOperation(value = "Atualiza um Cliente", response = ClienteDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cliente atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum cliente encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @PutMapping("atualizarClientePorId/{id}")
    public ResponseEntity<ClienteDTO> atualizarClientePorId(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteAtualizadoDTO) {

        ClienteDTO clienteAtualizadoResponseDTO = clienteUseCase.atualizarClientePorId(id, clienteAtualizadoDTO);
        return new ResponseEntity<>(clienteAtualizadoResponseDTO, HttpStatus.OK);
    }
}