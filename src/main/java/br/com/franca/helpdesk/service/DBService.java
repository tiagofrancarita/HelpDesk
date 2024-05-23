package br.com.franca.helpdesk.service;


import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.Cliente;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.enums.Perfil;
import br.com.franca.helpdesk.domains.enums.PrioridadeEnum;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import br.com.franca.helpdesk.repositorys.ChamadosRepository;
import br.com.franca.helpdesk.repositorys.ClienteRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    public void instacieDB() {

        Tecnico T1 = new Tecnico(null, "Tecnico 1", "43830635001","tecnico@email.com","@Biatico681");
        T1.addPerfil(Perfil.ADMIN);
        tecnicoRepository.saveAll(Arrays.asList(T1));

        Cliente C1 = new Cliente(null, "Cliente 1", "42193087091","cliente@email.com","@Biatico681");
        clienteRepository.saveAll(Arrays.asList(C1));

        Chamado CH1 = new Chamado(null, "Tela Azul", "Ao Ligar o equipamento, no carregamento do windows 10, usuario informa que aparece tela azul sem a possibilidade de login",
                PrioridadeEnum.MEDIA, StatusEnum.ABERTO, "Cliente solicita breve prioridade no atendimento, cliente da empresa a 10 anos", C1, T1);
        chamadosRepository.saveAll(Arrays.asList(CH1));

    }
}