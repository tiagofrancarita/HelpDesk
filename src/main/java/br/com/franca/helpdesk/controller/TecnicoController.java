package br.com.franca.helpdesk.controller;

import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.dtos.TecnicoDTO;
import br.com.franca.helpdesk.exceptions.ErrorDetails;
import br.com.franca.helpdesk.exceptions.TecnicoNotExludeAssociantioTicket;
import br.com.franca.helpdesk.exceptions.TecnicosNotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
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

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping(value = "/pessoa", method = RequestMethod.GET, produces="application/json")
@RequestMapping(value = "v1/tecnicos", produces = "application/json")
@Api(value = "Tecnico Management System", description = "Operações referentes ao técnico no Sistema de Helpdesk")
public class TecnicoController {

    private TecnicoRepository tecnicoRepository;
    private TecnicosUseCase tecnicosUseCase;
    private ChamadosRepository chamadosRepository;

    private Logger log = LoggerFactory.getLogger(TecnicoController.class);

    @Autowired
    public TecnicoController(TecnicoRepository tecnicoRepository, TecnicosUseCase tecnicosUseCase) {
        this.tecnicoRepository = tecnicoRepository;
        this.tecnicosUseCase = tecnicosUseCase;
    }

    @ExceptionHandler(TecnicosNotFoundException.class)
    public ResponseEntity<String> handleNoTecnicosFoundException(TecnicosNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(TecnicoNotExludeAssociantioTicket.class)
    public ResponseEntity<String> handleTecnicoNotExludeAssociantioTicket(TecnicoNotExludeAssociantioTicket e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
    }


    @GetMapping("/listarTecnicos")
    @ApiOperation(value = "Lista todos os técnicos", response = TecnicoDTO.class, responseContainer = "List", produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Técnicos listados com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum técnico encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),

    })
    public ResponseEntity<List<TecnicoDTO>> listarTecnicos() {
        try {
            List<TecnicoDTO> tecnicos = tecnicosUseCase.listarTecnicos();
            return new ResponseEntity<>(tecnicos, HttpStatus.OK);
        } catch (TecnicosNotFoundException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Busca um tecnico cadastrado pelo id", response = TecnicoDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Técnicos listados com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum técnico encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),

    })
    @GetMapping(value = "buscarTecnicoPorId/{id}")
    public ResponseEntity<?> buscarTecnicoPorId(@PathVariable Long id) {
        try {
            TecnicoDTO tecnicoDTO = tecnicosUseCase.buscarPorId(id).getBody();
            return ResponseEntity.ok(tecnicoDTO);
        } catch (TecnicosNotFoundException ex) {
            String message = ex.getMessage();
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Object NOT FOUND", "Tecnico não encontrado ID:" + id , " /v1/tecnicos/buscarTecnicoPorId/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
        }

    }

    @PostMapping("/cadastrarTecnico")
    @ApiOperation(value = "Cadastra um novo técnico", response = TecnicoDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Técnico cadastrado com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao cadastrar tecnico"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),

    })
    public ResponseEntity<?> cadastrarTecnico(@RequestBody @Valid TecnicoDTO tecnicoDTO) {
        try {
            TecnicoDTO novoTecnico = tecnicosUseCase.cadastrarTecnico(tecnicoDTO);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(novoTecnico.getId()).toUri();
            return ResponseEntity.created(uri).body(novoTecnico); // Correct way to create ResponseEntity
        } catch (TecnicosNotFoundException e) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Object NOT FOUND", "Erro ao Cadastrar tecnico", " /v1/tecnicos/cadastrarTecnico");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);

        }
    }

    @ApiOperation(value = "Deleta um técnico pelo id", response = TecnicoDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teccnico excluído com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum técnico encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),

    })
    @DeleteMapping("deletarTecnicoPorId/{id}")
    public ResponseEntity<?> deletarTecnicoPorId(@PathVariable Long id) {
        try {
            tecnicosUseCase.deletarTecnico(id);
            return new ResponseEntity<>("Técnico excluído com sucesso", HttpStatus.OK);
        } catch (TecnicosNotFoundException e) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Object NOT FOUND", "ID inválido", " /v1/tecnicos/deletarTecnicoPorId/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
        } catch (TecnicoNotExludeAssociantioTicket e) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Object NOT FOUND", "Erro ao Excluir o tecnico, há tickets abertos associados a ele", " /v1/tecnicos/deletarTecnicoPorId/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Object NOT FOUND", "Erro interno do servidor", " /v1/tecnicos/deletarTecnicoPorId/" + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);        }
    }

    @ApiOperation(value = "Atualiza um técnico", response = TecnicoDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Técnicos atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum técnico encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),

    })
    @PutMapping("atualizarTecnico/{id}")
    public ResponseEntity<TecnicoDTO> atualizarTecnico(@PathVariable Long id, @Valid @RequestBody TecnicoDTO tecnicoAtualizadoDTO) {
        // Busca o técnico existente pelo ID
        Optional<Tecnico> optionalTecnico = tecnicoRepository.findById(id);
        if (!optionalTecnico.isPresent()) {
            throw new EntityNotFoundException("Técnico não encontrado");
        }
        Tecnico tecnicoExistente = optionalTecnico.get();

        // Atualiza os campos do técnico existente com os valores do DTO
        tecnicoExistente.setNome(tecnicoAtualizadoDTO.getNome());
        tecnicoExistente.setEmail(tecnicoAtualizadoDTO.getEmail());
        tecnicoExistente.setSenha(tecnicoAtualizadoDTO.getSenha());
        // Atualize outros campos conforme necessário

        // Chama o método de atualização do caso de uso e converte o resultado para DTO
        TecnicoDTO tecnicoAtualizadoResponseDTO = tecnicosUseCase.atualizarTecnico(id, tecnicoAtualizadoDTO);

        // Retorna o ResponseEntity com o objeto TecnicoDTO atualizado
        return new ResponseEntity<>(tecnicoAtualizadoResponseDTO, HttpStatus.OK);
    }
}