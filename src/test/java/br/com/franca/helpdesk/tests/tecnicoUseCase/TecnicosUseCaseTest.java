package br.com.franca.helpdesk.tests.tecnicoUseCase;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.exceptions.TecnicosNotFoundException;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import br.com.franca.helpdesk.usecases.TecnicosUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        when(tecnicoRepository.findAll()).thenReturn(List.of(tecnico));

        List<Tecnico> tecnicos = tecnicosUseCase.listarTecnicos();

        assertNotNull(tecnicos);
        assertFalse(tecnicos.isEmpty());
        assertEquals(tecnico.getId(), tecnicos.get(0).getId());

        verify(tecnicoRepository, times(1)).findAll();
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

        Tecnico foundTecnico = tecnicosUseCase.buscarPorId(1L);

        assertNotNull(foundTecnico);
        assertEquals(tecnico.getId(), foundTecnico.getId());

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
        when(tecnicoRepository.findByEmail(tecnico.getEmail())).thenReturn(Optional.empty());
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);

        Tecnico novoTecnico = tecnicosUseCase.cadastrarTecnico(tecnico);

        assertNotNull(novoTecnico);
        assertEquals(tecnico.getEmail(), novoTecnico.getEmail());

        verify(tecnicoRepository, times(1)).findByEmail(tecnico.getEmail());
        verify(tecnicoRepository, times(1)).save(tecnico);
    }

    @Test
    public void testCadastrarTecnicoEmailExistente() {
        when(tecnicoRepository.findByEmail(tecnico.getEmail())).thenReturn(Optional.of(tecnico));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.cadastrarTecnico(tecnico);
        });

        assertEquals("Email já está em uso", exception.getMessage());

        verify(tecnicoRepository, times(1)).findByEmail(tecnico.getEmail());
        verify(tecnicoRepository, never()).save(any());
    }

    @Test
    public void testCadastrarTecnicoNomeObrigatorio() {
        tecnico.setNome(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.cadastrarTecnico(tecnico);
        });

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    public void testCadastrarTecnicoSenhaInvalida() {
        tecnico.setSenha("senha");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.cadastrarTecnico(tecnico);
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
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnico));
        when(tecnicoRepository.save(tecnico)).thenReturn(tecnico);

        Tecnico tecnicoAtualizado = new Tecnico();
        tecnicoAtualizado.setNome("Tecnico Atualizado");
        tecnicoAtualizado.setEmail("atualizado@teste.com");
        tecnicoAtualizado.setSenha("Senha1234!");
        tecnicoAtualizado.getPerfis().clear();
        tecnicoAtualizado.getPerfis().addAll(tecnicoAtualizado.getPerfis());

        Tecnico updatedTecnico = tecnicosUseCase.atualizarTecnico(1L, tecnicoAtualizado);

        assertNotNull(updatedTecnico);
        assertEquals(tecnicoAtualizado.getNome(), updatedTecnico.getNome());

        verify(tecnicoRepository, times(1)).findById(1L);
        verify(tecnicoRepository, times(1)).save(tecnico);
    }

    @Test
    public void testAtualizarTecnicoIdInvalido() {

        assertThrows(IllegalArgumentException.class, () -> tecnicosUseCase.atualizarTecnico(0L, tecnico));
    }

    @Test
    public void testAtualizarTecnicoNaoEncontrado() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tecnicosUseCase.atualizarTecnico(1L, tecnico));

        verify(tecnicoRepository, times(1)).findById(1L);
    }

    @Test
    void testAtualizarTecnicoNomeObrigatorio() {
        // Simula a existência do técnico
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(new Tecnico()));

        // Define o nome do técnico como nulo
        tecnico.setNome(null);

        // Verifica se a exceção ValidationException é lançada e se a mensagem é a esperada
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.atualizarTecnico(1L, tecnico);
        });

        assertEquals("Nome é obrigatório", exception.getMessage());

        // Verifica se findById foi chamado uma vez
        verify(tecnicoRepository, times(1)).findById(1L);
    }

    @Test
    void testAtualizarTecnicoEmailObrigatorio() {
        // Simula a existência do técnico
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(new Tecnico()));

        // Define o nome do técnico como nulo
        tecnico.setNome("Teste Email OBGTORIO");
        tecnico.setEmail(null);

        // Verifica se a exceção ValidationException é lançada e se a mensagem é a esperada
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.atualizarTecnico(1L, tecnico);
        });

        assertEquals("O Campo e-mail é obrigatório", exception.getMessage());

        // Verifica se findById foi chamado uma vez
        verify(tecnicoRepository, times(1)).findById(1L);
    }

    @Test
    void testAtualizarTecnicoSenhaObrigatorio() {
        // Simula a existência do técnico
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(new Tecnico()));

        // Define o nome do técnico como nulo
        tecnico.setNome("Teste Email OBGTORIO");
        tecnico.setEmail("tiagofranca.rita@gmail.com");
        tecnico.setSenha(null);

        // Verifica se a exceção ValidationException é lançada e se a mensagem é a esperada
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tecnicosUseCase.atualizarTecnico(1L, tecnico);
        });

        assertEquals("O Campo senha é obrigatório", exception.getMessage());

        // Verifica se findById foi chamado uma vez
        verify(tecnicoRepository, times(1)).findById(1L);
    }
}
