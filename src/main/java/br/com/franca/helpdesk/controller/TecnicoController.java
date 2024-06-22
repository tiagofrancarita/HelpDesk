package br.com.franca.helpdesk.controller;

import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.dtos.TecnicoDTO;
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



    @GetMapping("/listarTecnicos")
    @ApiOperation(value = "Lista todos os técnicos", response = TecnicoDTO.class, responseContainer = "List", produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Técnicos listados com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum técnico encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),

    })
    public ResponseEntity<List<TecnicoDTO>> listarTecnicos() {

            List<TecnicoDTO> tecnicos = tecnicosUseCase.listarTecnicos();
            return new ResponseEntity<>(tecnicos, HttpStatus.OK);

    }

    @ApiOperation(value = "Busca um tecnico cadastrado pelo id", response = TecnicoDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Técnicos listados com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum técnico encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),

    })
    @GetMapping(value = "buscarTecnicoPorId/{id}")
    public ResponseEntity<?> buscarTecnicoPorId(@PathVariable Long id) {

            TecnicoDTO tecnicoDTO = tecnicosUseCase.buscarPorId(id).getBody();

            return ResponseEntity.ok(tecnicoDTO);


    }

    @PostMapping("/cadastrarTecnico")
    @ApiOperation(value = "Cadastra um novo técnico", response = TecnicoDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "POST")
    public ResponseEntity<TecnicoDTO> create(@Valid @RequestBody TecnicoDTO tecnicoDTO) {
        Tecnico newObj = tecnicosUseCase.cadastrarTecnico(tecnicoDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newObj.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @ApiOperation(value = "Deleta um técnico pelo id", response = TecnicoDTO.class, produces = "application/json", consumes = "application/json", httpMethod = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Teccnico excluído com sucesso"),
            @ApiResponse(code = 404, message = "Nenhum técnico encontrado"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),

    })
    @DeleteMapping("deletarTecnicoPorId/{id}")
    public ResponseEntity<?> deletarTecnicoPorId(@PathVariable Long id) {

            tecnicosUseCase.deletarTecnico(id);
            return new ResponseEntity<>("Técnico excluído com sucesso", HttpStatus.OK);

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