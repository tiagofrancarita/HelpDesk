package br.com.franca.helpdesk.controller;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.dtos.TecnicoDTO;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.exceptions.ErrorDetails;
import br.com.franca.helpdesk.exceptions.TecnicosNotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import br.com.franca.helpdesk.usecases.TecnicosUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "v1/tecnicos")
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


    @GetMapping("/listarTecnicos")
    public ResponseEntity<List<TecnicoDTO>> listarTecnicos() {
        try {
            List<TecnicoDTO> tecnicos = tecnicosUseCase.listarTecnicos();
            return new ResponseEntity<>(tecnicos, HttpStatus.OK);
        } catch (TecnicosNotFoundException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    /*
        private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
     */

    @PostMapping("/cadastrarTecnico")
    public ResponseEntity<Object> cadastrarTecnico(@RequestBody @Valid TecnicoDTO tecnicoDTO) {
        try {
            TecnicoDTO novoTecnico = tecnicosUseCase.cadastrarTecnico(tecnicoDTO);
            return new ResponseEntity<>(novoTecnico, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno no servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("deletarTecnicoPorId/{id}")
    public ResponseEntity<String> deletarTecnicoPorId(@PathVariable Long id) {
        try {
            tecnicosUseCase.deletarTecnico(id);
            return new ResponseEntity<>("Técnico excluído com sucesso", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("ID inválido", HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Técnico não encontrado", HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno no servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
        tecnicoExistente.setCpf(tecnicoAtualizadoDTO.getCpf());
        tecnicoExistente.setEmail(tecnicoAtualizadoDTO.getEmail());
        tecnicoExistente.setSenha(tecnicoAtualizadoDTO.getSenha());
        // Atualize outros campos conforme necessário

        // Chama o método de atualização do caso de uso e converte o resultado para DTO
        TecnicoDTO tecnicoAtualizadoResponseDTO = tecnicosUseCase.atualizarTecnico(id, tecnicoAtualizadoDTO);

        // Retorna o ResponseEntity com o objeto TecnicoDTO atualizado
        return new ResponseEntity<>(tecnicoAtualizadoResponseDTO, HttpStatus.OK);
    }
}