package br.com.franca.helpdesk.controller;

import br.com.franca.helpdesk.domains.OrdemServico;
import br.com.franca.helpdesk.domains.dtos.OrdemServicoDTO;
import br.com.franca.helpdesk.usecases.OrdemServicoUseCase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "v1/ordemServicos", produces = "application/json")
@Api(value = "Ordem Service Management System", description = "Operações referentes a ordens de serviço no sistema de Helpdesk")
public class OrdemServicoController {

    private Logger log = LoggerFactory.getLogger(OrdemServicoController.class);
    private final OrdemServicoUseCase ordemServicoUseCase;

    @Autowired
    public OrdemServicoController(OrdemServicoUseCase ordemServicoUseCase) {
        this.ordemServicoUseCase = ordemServicoUseCase;
    }

    @GetMapping("/listarOrdemServico")
    @ApiOperation(value = "Lista todas as ordem de serviço cadastradas", response = OrdemServico.class, responseContainer = "List", produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ordem de serviço listadas com sucesso"),
            @ApiResponse(code = 404, message = "Nenhuma ordem de serviço encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),

    })
    public ResponseEntity<List<OrdemServico>> listarOrdemServico() {

        List<OrdemServico> listagemOs = ordemServicoUseCase.listarOs();
        return new ResponseEntity<>(listagemOs, HttpStatus.OK);

    }

    @ApiOperation(value = "Busca uma ordem de serviço cadastrada pelo ID", response = OrdemServico.class, produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ordem de serviço encontrada com sucesso"),
            @ApiResponse(code = 404, message = "Nenhuma ordem de serviço encontrada"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção")
    })
    @GetMapping(value = "/buscarOrdemServicoPorId/{id}")
    public ResponseEntity<?> buscarOrdemServicoPorId(@PathVariable Long id) {
        try {
            OrdemServico buscarOrdemServicoId = ordemServicoUseCase.buscarOrdemServicoPorId(id);
            return ResponseEntity.ok(buscarOrdemServicoId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma ordem de serviço encontrada com o ID fornecido");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao buscar a ordem de serviço");
        }
    }


    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @PostMapping(value = "/criarOrdemServico")
    public ResponseEntity<OrdemServicoDTO> criarOrdemServico(@RequestBody OrdemServicoDTO ordemServico) {
        OrdemServicoDTO responseDTO = ordemServicoUseCase.criarOrdemServico(ordemServico);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("atualizarStatusParaExecucao/{id}")
    public ResponseEntity<OrdemServico> atualizarStatusParaExecucao(@PathVariable Long id) {
        OrdemServico ordemServicoAtualizada = ordemServicoUseCase.atualizarStatusParaExecucao(id);
        return ResponseEntity.ok(ordemServicoAtualizada);
    }

    @PutMapping("atualizarStatusParaDevolvido/{id}")
    public ResponseEntity<OrdemServico> atualizarStatusParaDevolvido(@PathVariable Long id) {
        OrdemServico ordemServicoAtualizada = ordemServicoUseCase.atualizarStatusParaDevolvido(id);
        return ResponseEntity.ok(ordemServicoAtualizada);
    }

    @PutMapping("atualizarStatusParaCancelado/{id}")
    public ResponseEntity<OrdemServico> atualizarStatusParaCancelado(@PathVariable Long id) {
        OrdemServico ordemServicoAtualizada = ordemServicoUseCase.atualizarStatusParaCancelado(id);
        return ResponseEntity.ok(ordemServicoAtualizada);
    }

    @PutMapping("atualizarStatusParaEncerrado/{id}")
    public ResponseEntity<OrdemServico> atualizarStatusParaEncerrado(@PathVariable Long id) {
        OrdemServico ordemServicoAtualizada = ordemServicoUseCase.atualizarStatusParaEncerrado(id);
        return ResponseEntity.ok(ordemServicoAtualizada);
    }

    @PutMapping("atualizarStatusParaAberto/{id}")
    public ResponseEntity<OrdemServico> atualizarStatusParaAberto(@PathVariable Long id) {
        OrdemServico ordemServicoAtualizada = ordemServicoUseCase.atualizarStatusParaAberto(id);
        return ResponseEntity.ok(ordemServicoAtualizada);
    }
}