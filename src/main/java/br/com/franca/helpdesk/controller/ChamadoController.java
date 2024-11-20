package br.com.franca.helpdesk.controller;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.dtos.ChamadosDTO;
import br.com.franca.helpdesk.domains.dtos.ClienteDTO;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.usecases.ChamadoUseCase;
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
import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ApiOperation(value = "Busca chamados por status", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Chamados listados com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum chamado encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @GetMapping("/countByStatus")
    public ResponseEntity<?> getCountByStatus() {
        try {
            log.info("Recuperando contagem dos chamados por status...");
            List<Object[]> result = chamadoUseCase.countByStatus();
            log.info("Contagem de chamados por status recuperada com sucesso");
            // Preparar um mapa para armazenar o resultado
            Map<String, Long> countByStatusMap = new HashMap<>();
            log.info("Iterando sobre a lista de arrays de objetos...");
            // Iterar sobre a lista de arrays de objetos
            for (Object[] row : result) {
                String status = (String) row[0];  // Supondo que o primeiro elemento seja uma String (status)
                BigInteger count = (BigInteger) row[1];  // Usando BigInteger para lidar com o valor retornado
            log.info("Convertendo BigInteger para Long...");
                // Convertendo BigInteger para Long
                Long countLong = count.longValue();
                log.info("Adicionando contagem por status ao mapa...");
                countByStatusMap.put(status, countLong);
            }
            log.info("Retornando contagem por status...");
            return ResponseEntity.ok(countByStatusMap);
        } catch (Exception e) {
            log.error("Erro ao recuperar contagem por status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao recuperar contagem por status: " + e.getMessage());
        }
    }

    @ApiOperation(value = "Schedule de verificação chamados abertos 7 dias ou mais", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Alerta enviado com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum alerta encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @GetMapping("/verificaChamadosAbertosHa7DiasOuMais")
    public ResponseEntity<Boolean> verificaChamadosAbertosHa7DiasOuMais() {
        boolean existemChamadosAbertos = chamadoUseCase.verificaChamadosAbertosHa7DiasOuMais();
        return ResponseEntity.ok(existemChamadosAbertos);
    }

    @ApiOperation(value = "Busca um chamado cadastrado pelo id", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Chamado listados com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum chamado encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @GetMapping(value = "buscarChamado/{id}")
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
    public ResponseEntity<Void> cadastroChamado(@Valid @RequestBody ChamadosDTO chamadosDTO) {

        Chamado novoChamado = chamadoUseCase.cadastrarChamado(chamadosDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(novoChamado.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @ApiOperation(value = "Atualiza informações de um chamado", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Chamado atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao atualizar Chamado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @PutMapping("atualizaInfoChamado/{id}")
    public ResponseEntity<Chamado> atualizaInfoChamado(@PathVariable Long id, @RequestBody @Valid ChamadosDTO objDTO) {
        Chamado chamadoAtualizado = chamadoUseCase.atualizaInfoChamado(id, objDTO);
        return ResponseEntity.ok(chamadoAtualizado);
    }

    @ApiOperation(value = "Atualiza o status do chamado para execução", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "chamado atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao atualizar chamado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @PutMapping("atualizaChamadoParaExecucao/{id}")
    public ResponseEntity<String> atualizaChamadoParaExecucao(@PathVariable Long id) {

        chamadoUseCase.atualizarChamadoParaStatusEmExecucao(id);
        return new ResponseEntity<String>("Chamado atualizado para execução com sucesso", HttpStatus.OK);

    }

    @ApiOperation(value = "Atualiza o status do chamado para encerrado", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "chamado atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao atualizar chamado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @PutMapping("atualizaChamadoParaEncerrado/{id}")
    public ResponseEntity<String> atualizaChamadoParaEncerrado(@PathVariable Long id) {

        chamadoUseCase.atualizarChamadoParaStatusEncerrado(id);
        return new ResponseEntity<String>("Chamado atualizado para encerrado com sucesso", HttpStatus.OK);

    }

    @ApiOperation(value = "Atualiza o status do chamado para cancelado", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "chamado atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao atualizar chamado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @PutMapping("atualizaChamadoParaCancelado/{id}")
    public ResponseEntity<String> atualizaChamadoParaCancelado(@PathVariable Long id) {

        chamadoUseCase.atualizarChamadoParaStatusCancelado(id);
        return new ResponseEntity<String>("Chamado atualizado para cancelado com sucesso", HttpStatus.OK);

    }

    @ApiOperation(value = "Atualiza o status do chamado para devolvido", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "chamado atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao atualizar chamado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @PutMapping("atualizaChamadoParaDevolvido/{id}")
    public ResponseEntity<String> atualizaChamadoParaDevolvido(@PathVariable Long id) {

        chamadoUseCase.atualizarChamadoParaStatusDevolvido(id);
        return new ResponseEntity<String>("Chamado atualizado para devolvido com sucesso", HttpStatus.OK);

    }

    @ApiOperation(value = "Atualiza o status do chamado para aberto", response = ChamadosDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "chamado atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao atualizar chamado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @PutMapping("atualizaChamadoParaAberto/{id}")
    public ResponseEntity<String> atualizaChamadoParaAberto(@PathVariable Long id) {

        chamadoUseCase.atualizarChamadoParaStatusAberto(id);
        return new ResponseEntity<String>("Chamado atualizado para aberto com sucesso", HttpStatus.OK);

    }
}
