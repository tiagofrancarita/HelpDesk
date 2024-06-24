package br.com.franca.helpdesk.service;


import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Cliente;
import br.com.franca.helpdesk.domains.OrdemServico;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.enums.Perfil;
import br.com.franca.helpdesk.domains.enums.PrioridadeEnum;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.ClienteRepository;
import br.com.franca.helpdesk.repositorys.OrdemServicoRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import br.com.franca.helpdesk.usecases.OrdemServicoUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class DBService {


    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ChamadosRepository chamadosRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private OrdemServicoUseCase ordemServicoUseCase;


    public void instacieDB() {

        Tecnico T1 = new Tecnico(null, "Tecnico 1", "43830635001","tecnico@email.com",bCryptPasswordEncoder.encode("@Biatico681"));
        T1.addPerfil(Perfil.ADMIN);
        tecnicoRepository.saveAll(Arrays.asList(T1));

        Cliente C1 = new Cliente(null, "Cliente 1", "42193087091","cliente@email.com",bCryptPasswordEncoder.encode("@Biatico681"));
        clienteRepository.saveAll(Arrays.asList(C1));

        Chamado CH1 = new Chamado(null, "Tela Azul", "Ao Ligar o equipamento, no carregamento do windows 10, usuario informa que aparece tela azul sem a possibilidade de login",
                PrioridadeEnum.MEDIA, StatusEnum.ABERTO, "Cliente solicita breve prioridade no atendimento, cliente da empresa a 10 anos", C1, T1);
        chamadosRepository.saveAll(Arrays.asList(CH1));

        String numeroChamado = ordemServicoUseCase.gerarNumeroChamado();

        OrdemServico OS1 = new OrdemServico(null,CH1,numeroChamado,  "Problema na tela azul", CH1.getDataAbertura(), CH1.getDataFechamento(), "Problema na tela azul", "Tratativa", "Solucao", StatusEnum.ABERTO);
        ordemServicoRepository.saveAll(Arrays.asList(OS1));

        //    public OrdemServico(Long id, Chamado chamado, String numeroChamado, String descricao, LocalDateTime dataCriacao, LocalDateTime dataFechamento, String problema, String tratativa, String solucao, StatusEnum statusEnum) {

       // null, CH1, , "Problema na tela azul", CH1.getDataAbertura(), CH1.getDataFechamento(), "Problema na tela azul", "Tratativa", "Solucao", StatusEnum.ABERTO

    }
}