package br.com.franca.helpdesk.controller;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.exceptions.TecnicosNotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import br.com.franca.helpdesk.usecases.TecnicosUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

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
    public ResponseEntity<List<Tecnico>> listarTecnicos() {
        try {
            List<Tecnico> tecnicos = tecnicosUseCase.listarTecnicos();
            return new ResponseEntity<>(tecnicos, HttpStatus.OK);
        } catch (TecnicosNotFoundException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("buscarTecnicoPorId/{id}")
    public ResponseEntity<Object> buscarTecnicoPorId(@PathVariable Long id) {
        try {
            Tecnico tecnico = tecnicosUseCase.buscarPorId(id);
            return new ResponseEntity<>(tecnico, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.info("ID inválido: " + id);
            return new ResponseEntity<>("ID inválido", HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            log.info("Técnico com ID " + id + " não encontrado");
            return new ResponseEntity<>("Técnico não encontrado", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Erro ao buscar técnico com ID " + id, e);
            return new ResponseEntity<>("Erro interno no servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cadastrarTecnico")
    public ResponseEntity<Object> cadastrarTecnico(@RequestBody @Valid Tecnico tecnico) {
        try {
            Tecnico novoTecnico = tecnicosUseCase.cadastrarTecnico(tecnico);
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
    public ResponseEntity<Tecnico> atualizarTecnico(@PathVariable Long id, @Valid @RequestBody Tecnico tecnicoAtualizado) {
        Tecnico tecnicoAtualizadoResponse = tecnicosUseCase.atualizarTecnico(id, tecnicoAtualizado);
        return new ResponseEntity<>(tecnicoAtualizadoResponse, HttpStatus.OK);
    }
}