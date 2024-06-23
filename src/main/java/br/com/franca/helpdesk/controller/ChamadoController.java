package br.com.franca.helpdesk.controller;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Cliente;
import br.com.franca.helpdesk.domains.dtos.ChamadosDTO;
import br.com.franca.helpdesk.domains.dtos.ClienteDTO;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.ClienteRepository;
import br.com.franca.helpdesk.usecases.ChamadoUseCase;
import br.com.franca.helpdesk.usecases.ClienteUseCase;
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
import java.util.Optional;

@RestController
@RequestMapping(value = "v1/chamados", produces = "application/json")
@Api(value = "Chamados Management System", description = "Operações referentes ao chamados no Sistema de Helpdesk")
public class ChamadoController {

    private Logger log = LoggerFactory.getLogger(ChamadoController.class);

    private final ChamadosRepository chamadosRepository;
    private final ChamadoUseCase chamadoUseCase;

    @Autowired
    public ChamadoController(ChamadosRepository chamadosRepository, ChamadoUseCase chamadoUseCase) {
        this.chamadosRepository = chamadosRepository;
        this.chamadoUseCase = chamadoUseCase;
    }

    @ApiOperation(value = "Busca um chamado cadastrado pelo id", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Chamado listados com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum chamado encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @GetMapping(value = "buscarChamadoPorId/{id}")
    public ResponseEntity<?> buscarChamadoPorId(@PathVariable Long id) {

        ResponseEntity<ChamadosDTO> buscarChamadoPorId = chamadoUseCase.buscarChamadoPorId(id);
        return ResponseEntity.ok(buscarChamadoPorId);

    }

    @GetMapping("/listarChamados")
    @ApiOperation(value = "Lista todos os chamados cadastrados", response = ChamadosDTO.class, responseContainer = "List", produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Chamados listados com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum chamado encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity<List<ChamadosDTO>> listarChamados() {

        List<ChamadosDTO> listagemChamadosCadastrados = chamadoUseCase.listarChamados();
        return new ResponseEntity<>(listagemChamadosCadastrados, HttpStatus.OK);

    }

    @ApiOperation(value = "Cadastra um novo chamado", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Chamado criado com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao criar Chamado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @PostMapping("/cadastroChamado")
    public ResponseEntity<ClienteDTO> cadastroChamado(@Valid @RequestBody ChamadosDTO chamadosDTO) {

        Chamado novoChamado = chamadoUseCase.cadastrarChamado(chamadosDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(novoChamado.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }
}
