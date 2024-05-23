package br.com.franca.helpdesk.tests.tecnicoUseCase;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.dtos.TecnicoDTO;
import br.com.franca.helpdesk.domains.enums.Perfil;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.exceptions.TecnicosNotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import br.com.franca.helpdesk.usecases.TecnicosUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityNotFoundException;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TecnicosUseCaseTest {

    @Mock
    private TecnicoRepository tecnicoRepository;

    @Mock
    private ChamadosRepository chamadosRepository;

    @InjectMocks
    private TecnicosUseCase tecnicosUseCase;

    private Tecnico tecnico;

    @BeforeEach
    void setUp() {
        tecnico = new Tecnico();
        tecnico.setId(1L);
        tecnico.setNome("Teste Tecnico");
        tecnico.setEmail("teste@teste.com");
        tecnico.setSenha("Senha123!");
        tecnico.setDataCriacao(LocalDateTime.now());
        tecnico.getPerfis().addAll(tecnico.getPerfis());

    }

    @Test
    public void testListarTecnicos() {
        // Dados de entrada
        Tecnico tecnico1 = new Tecnico(1L, "Técnico 1", "12345678900", "tecnico1@example.com", "senha123");
        tecnico1.addPerfil(Perfil.TECNICO);
        Tecnico tecnico2 = new Tecnico(2L, "Técnico 2", "09876543211", "tecnico2@example.com", "senha456");
        tecnico2.addPerfil(Perfil.ADMIN);

        // Mock do repositório
        TecnicoRepository tecnicoRepository = mock(TecnicoRepository.class);
        when(tecnicoRepository.findAll()).thenReturn(Arrays.asList(tecnico1, tecnico2));

        // Instância do caso de uso
        ChamadosRepository chamadoRepository = mock(ChamadosRepository.class); // Mock do chamadoRepository
        TecnicosUseCase tecnicoUseCase = new TecnicosUseCase(tecnicoRepository, chamadoRepository);

        // Execução do método
        List<TecnicoDTO> tecnicoDTOs = tecnicoUseCase.listarTecnicos();

        // Verificações
        assertNotNull(tecnicoDTOs);
        assertEquals(2, tecnicoDTOs.size());

        TecnicoDTO dto1 = tecnicoDTOs.get(0);
        assertEquals(tecnico1.getId(), dto1.getId());
        assertEquals(tecnico1.getNome(), dto1.getNome());
        assertEquals(tecnico1.getCpf(), dto1.getCpf());
        assertEquals(tecnico1.getEmail(), dto1.getEmail());
        assertEquals(tecnico1.getSenha(), dto1.getSenha());
        assertEquals(tecnico1.getDataCriacao(), dto1.getDataCriacao());
        assertEquals(tecnico1.getPerfis(), dto1.getPerfis());

        TecnicoDTO dto2 = tecnicoDTOs.get(1);
        assertEquals(tecnico2.getId(), dto2.getId());
        assertEquals(tecnico2.getNome(), dto2.getNome());
        assertEquals(tecnico2.getCpf(), dto2.getCpf());
        assertEquals(tecnico2.getEmail(), dto2.getEmail());
        assertEquals(tecnico2.getSenha(), dto2.getSenha());
        assertEquals(tecnico2.getDataCriacao(), dto2.getDataCriacao());
        assertEquals(tecnico2.getPerfis(), dto2.getPerfis());
    }

    @Test
    public void testListarTecnicosEmpty() {
        when(tecnicoRepository.findAll()).thenReturn(Collections.emptyList());

        TecnicosNotFoundException exception = assertThrows(TecnicosNotFoundException.class, () -> {
            tecnicosUseCase.listarTecnicos();
        });

        assertEquals("Nenhum técnico encontrado.", exception.getMessage());

        verify(tecnicoRepository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorId() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));

        ResponseEntity<TecnicoDTO> foundTecnico = tecnicosUseCase.buscarPorId(1L);

        assertNotNull(foundTecnico);
        assertEquals(tecnico.getId(), foundTecnico.getBody().getId());

        verify(tecnicoRepository, times(1)).findById(1L);
    }

    @Test
    public void testBuscarPorIdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> tecnicosUseCase.buscarPorId(0L));
    }

    @Test
    public void testBuscarPorIdNaoEncontrado() {
        long idNaoExistente = 50L;
        when(tecnicoRepository.findById(idNaoExistente)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tecnicosUseCase.buscarPorId(idNaoExistente);
        });

        assertEquals("Técnico não encontrado", exception.getMessage());

        verify(tecnicoRepository, times(1)).findById(idNaoExistente);
    }

    @Test
    public void testCadastrarTecnico() {
        // Dados de entrada
        Set<Integer> perfis = new HashSet<>();
        perfis.add(Perfil.ADMIN.getCodigo()); // Adiciona o perfil ADMIN
        TecnicoDTO tecnicoDTO = new TecnicoDTO();
        tecnicoDTO.setNome("Técnico Antigo");
        tecnicoDTO.setCpf("12345678900");
        tecnicoDTO.setEmail("tecnico@oldexample.com");
        tecnicoDTO.setSenha("@Biatico68");
        tecnicoDTO.setDataCriacao(LocalDateTime.now());
        // Define os perfis

        // Mock do repositório
        TecnicoRepository tecnicoRepository = mock(TecnicoRepository.class);
        ChamadosRepository chamadoRepository = mock(ChamadosRepository.class);
        Tecnico tecnicoExistente = new Tecnico(1L, "Técnico Antigo", "12345678900", "tecnico@oldexample.com", "@Biatico68");
        tecnicoExistente.addPerfil(Perfil.TECNICO);
        when(tecnicoRepository.save(any(Tecnico.class))).thenReturn(tecnicoExistente);

        // Instância do caso de uso
        TecnicosUseCase tecnicoUseCase = new TecnicosUseCase(tecnicoRepository, chamadoRepository);

        // Execução do método
        TecnicoDTO result = tecnicoUseCase.cadastrarTecnico(tecnicoDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(tecnicoDTO.getNome(), result.getNome());
        assertEquals(tecnicoDTO.getEmail(), result.getEmail());
        assertEquals(tecnicoDTO.getSenha(), result.getSenha());
        assertEquals(tecnicoDTO.getPerfis(), result.getPerfis());

        verify(tecnicoRepository, times(1)).save(any(Tecnico.class));
    }

    @Test
    public void testCadastrarTecnicoEmailEmUso() {
        // Dados de entrada
        Set<Integer> perfis = new HashSet<>();
        perfis.add(Perfil.TECNICO.getCodigo());
        TecnicoDTO tecnicoDTO = new TecnicoDTO();
        tecnicoDTO.setNome("Técnico Teste");
        tecnicoDTO.setCpf("12345678900");
        tecnicoDTO.setEmail("tecnico@example.com");
        tecnicoDTO.setSenha("Senha123!");
        tecnicoDTO.setDataCriacao(LocalDateTime.now());


        // Mock do repositório
        TecnicoRepository tecnicoRepository = mock(TecnicoRepository.class);
        when(tecnicoRepository.findByEmail(tecnicoDTO.getEmail())).thenReturn(Optional.of(new Tecnico()));

        // Configuração do Validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Instância do caso de uso
        TecnicosUseCase tecnicoUseCase = new TecnicosUseCase(tecnicoRepository, chamadosRepository);

        // Execução do método
        assertThrows(ValidationException.class, () -> tecnicoUseCase.cadastrarTecnico(tecnicoDTO));

        // Verificações
        verify(tecnicoRepository, times(1)).findByEmail(tecnicoDTO.getEmail());
        verify(tecnicoRepository, never()).save(any(Tecnico.class));
    }

    @Test
    public void testCadastrarTecnicoNomeObrigatorio() {
        Set<Integer> perfis = new HashSet<>();
        perfis.add(Perfil.TECNICO.getCodigo());
        TecnicoDTO tecnicoDTO = new TecnicoDTO();
        tecnicoDTO.setNome(null); // Nome não informado
        tecnicoDTO.setCpf("12345678900");
        tecnicoDTO.setEmail("tecnico@example.com");
        tecnicoDTO.setSenha("Senha123!");
        tecnicoDTO.setDataCriacao(LocalDateTime.now());


        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.cadastrarTecnico(tecnicoDTO);
        });

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    public void testCadastrarTecnicoSenhaInvalida() {
        Set<Perfil> perfis = new HashSet<>();
        perfis.add(Perfil.TECNICO);
        TecnicoDTO tecnicoDTO = new TecnicoDTO();
        tecnicoDTO.setNome("Tiago França"); // Nome não informado
        tecnicoDTO.setCpf("12345678900");
        tecnicoDTO.setEmail("tecnico@example.com");
        tecnicoDTO.setSenha("Senha123");
        tecnicoDTO.setDataCriacao(LocalDateTime.now());


        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.cadastrarTecnico(tecnicoDTO);
        });


        assertEquals("A senha não atende aos critérios mínimos", exception.getMessage());
    }

    @Test
    public void testDeletarTecnico() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(chamadosRepository.findByTecnicoAndStatusEnum(tecnico, StatusEnum.ABERTO)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> tecnicosUseCase.deletarTecnico(1L));

        verify(tecnicoRepository, times(1)).findById(1L);
        verify(chamadosRepository, times(1)).findByTecnicoAndStatusEnum(tecnico, StatusEnum.ABERTO);
        verify(tecnicoRepository, times(1)).delete(tecnico);
    }

    @Test
    public void testDeletarTecnicoComChamadosAbertos() {
        Chamado chamadoAberto = new Chamado();
        chamadoAberto.setId(123L); // definir um ID para o chamado em aberto

        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(chamadosRepository.findByTecnicoAndStatusEnum(tecnico, StatusEnum.ABERTO)).thenReturn(List.of(chamadoAberto));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.deletarTecnico(1L);
        });

        String expectedMessage = String.format("Não é possível excluir o técnico com ID %d. Existem chamados abertos atribuídos a ele. Chamado ID: %d", tecnico.getId(), chamadoAberto.getId());
        assertEquals(expectedMessage, exception.getMessage());

        verify(tecnicoRepository, times(1)).findById(1L);
        verify(chamadosRepository, times(1)).findByTecnicoAndStatusEnum(tecnico, StatusEnum.ABERTO);
        verify(tecnicoRepository, never()).delete(tecnico);
    }

    @Test
    public void testDeletarTecnicoNaoEncontrado() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tecnicosUseCase.deletarTecnico(1L);
        });

        assertEquals("Técnico não encontrado", exception.getMessage());

        verify(tecnicoRepository, times(1)).findById(1L);
        verify(chamadosRepository, never()).findByTecnicoAndStatusEnum(any(), any());
    }

    @Test
    public void testAtualizarTecnico() {
        // Dados de entrada
        Set<Integer> perfis = new HashSet<>();
        perfis.add(Perfil.ADMIN.getCodigo());
        TecnicoDTO tecnicoDTO = new TecnicoDTO();
        tecnicoDTO.setNome("Técnico Teste");
        tecnicoDTO.setCpf("12345678900");
        tecnicoDTO.setEmail("tecnico@example.com");
        tecnicoDTO.setSenha("Senha123!");
        tecnicoDTO.setDataCriacao(LocalDateTime.now());


        // Mock do repositório
        TecnicoRepository tecnicoRepository = mock(TecnicoRepository.class);
        ChamadosRepository chamadoRepository = mock(ChamadosRepository.class);
        Tecnico tecnicoExistente = new Tecnico(1L, "Técnico Antigo", "12345678900", "tecnico@oldexample.com", "SenhaAntiga");
        tecnicoExistente.addPerfil(Perfil.TECNICO);
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnicoExistente));
        when(tecnicoRepository.save(any(Tecnico.class))).thenReturn(tecnicoExistente);

        // Instância do caso de uso
        TecnicosUseCase tecnicoUseCase = new TecnicosUseCase(tecnicoRepository, chamadoRepository);

        // Execução do método
        TecnicoDTO result = tecnicoUseCase.atualizarTecnico(1L, tecnicoDTO);

        // Verificações
        assertNotNull(result);
        assertEquals(tecnicoDTO.getNome(), result.getNome());
        assertEquals(tecnicoDTO.getEmail(), result.getEmail());
        assertEquals(tecnicoDTO.getSenha(), result.getSenha());
        assertEquals(tecnicoDTO.getPerfis(), result.getPerfis());

        verify(tecnicoRepository, times(1)).findById(1L);
        verify(tecnicoRepository, times(1)).save(any(Tecnico.class));
    }

    @Test
    public void testAtualizarTecnicoIdInvalido() {
        TecnicoDTO tecnicoDTO = new TecnicoDTO();
        tecnicoDTO.setNome("Técnico Teste");
        tecnicoDTO.setCpf("12345678900");
        tecnicoDTO.setEmail("tecnico@example.com");
        tecnicoDTO.setSenha("Senha123!");
        tecnicoDTO.setDataCriacao(LocalDateTime.now());
        Set<Integer> perfis = new HashSet<>();
        perfis.add(Perfil.TECNICO.getCodigo());


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tecnicosUseCase.atualizarTecnico(0L, tecnicoDTO);
        });

        assertEquals("O ID informado é inválido", exception.getMessage());
    }

    @Test
    public void testAtualizarTecnicoNaoEncontrado() {
        // Dados de entrada para o teste
        TecnicoDTO tecnicoDTO = new TecnicoDTO();
        tecnicoDTO.setId(1L);
        tecnicoDTO.setNome("Novo Nome");
        tecnicoDTO.setCpf("12345678900");
        tecnicoDTO.setEmail("novo_email@example.com");
        tecnicoDTO.setSenha("novaSenha123!");
        tecnicoDTO.setDataCriacao(LocalDateTime.now());
        Set<Integer> perfis = new HashSet<>();
        //perfis.add(Perfil.TECNICO.getCodigo());


        // Simula o repositório retornando um Optional vazio ao buscar o técnico pelo ID
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifica se uma EntityNotFoundException é lançada ao tentar atualizar o técnico
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tecnicosUseCase.atualizarTecnico(1L, tecnicoDTO);
        });

        // Verifica se o método findById do repositório foi chamado exatamente uma vez com o ID correto
        verify(tecnicoRepository, times(1)).findById(1L);
    }

    @Test
    void testAtualizarTecnicoNomeObrigatorio() {
        // Simula a existência do técnico
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(new Tecnico()));

        // Cria um objeto TecnicoDTO para representar a atualização do técnico com o nome nulo
        TecnicoDTO tecnicoDTO = new TecnicoDTO();
        tecnicoDTO.setNome(null);

        // Verifica se a exceção ValidationException é lançada e se a mensagem é a esperada
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.atualizarTecnico(1L, tecnicoDTO);
        });

        assertEquals("O Campo nome é obrigatório", exception.getMessage());

        // Verifica se findById foi chamado uma vez
        verify(tecnicoRepository, times(1)).findById(1L);
    }

    @Test
    void testAtualizarTecnicoEmailObrigatorio() {
        // Simula a existência do técnico
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(new Tecnico()));

        // Cria um objeto TecnicoDTO para representar a atualização do técnico com o e-mail nulo
        TecnicoDTO tecnicoDTO = new TecnicoDTO();
        tecnicoDTO.setNome("Teste Email Obrigatório");
        tecnicoDTO.setEmail(null);

        // Verifica se a exceção ValidationException é lançada e se a mensagem é a esperada
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.atualizarTecnico(1L, tecnicoDTO);
        });

        assertEquals("O Campo e-mail é obrigatório", exception.getMessage());

        // Verifica se findById foi chamado uma vez
        verify(tecnicoRepository, times(1)).findById(1L);
    }

    @Test
    void testAtualizarTecnicoSenhaObrigatorio() {

        // Simula a existência do técnico
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(new Tecnico()));

        // Cria um objeto TecnicoDTO para representar a atualização do técnico com a senha nula
        TecnicoDTO tecnicoDTO = new TecnicoDTO();
        tecnicoDTO.setNome("Teste Senha Obrigatória");
        tecnicoDTO.setEmail("tiagofranca.rita@gmail.com");
        tecnicoDTO.setSenha(null);

        // Verifica se a exceção ValidationException é lançada e se a mensagem é a esperada
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.atualizarTecnico(1L, tecnicoDTO);
        });

        assertEquals("O Campo senha é obrigatório", exception.getMessage());

        // Verifica se findById foi chamado uma vez
        verify(tecnicoRepository, times(1)).findById(1L);
    }
}