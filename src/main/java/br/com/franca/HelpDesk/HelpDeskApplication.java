package br.com.franca.HelpDesk;

import br.com.franca.HelpDesk.domains.Chamado;
import br.com.franca.HelpDesk.domains.Cliente;
import br.com.franca.HelpDesk.domains.Tecnico;
import br.com.franca.HelpDesk.domains.enums.PerfilEnum;
import br.com.franca.HelpDesk.domains.enums.PrioridadeEnum;
import br.com.franca.HelpDesk.domains.enums.StatusEnum;
import br.com.franca.HelpDesk.repositorys.ChamadosRepository;
import br.com.franca.HelpDesk.repositorys.ClienteRepository;
import br.com.franca.HelpDesk.repositorys.TecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class HelpDeskApplication implements CommandLineRunner {

	@Autowired
	private TecnicoRepository tecnicoRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ChamadosRepository chamadosRepository;

	public static void main(String[] args) {
		SpringApplication.run(HelpDeskApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Tecnico T1 = new Tecnico(null, "Tecnico 1", "43830635001","tecnico@email.com","@Biatico681");
				T1.addPerfil(PerfilEnum.ADMIN);
		tecnicoRepository.saveAll(Arrays.asList(T1));

		Cliente C1 = new Cliente(null, "Cliente 1", "42193087091","cliente@email.com","@Biatico681");
		clienteRepository.saveAll(Arrays.asList(C1));

		Chamado CH1 = new Chamado(null, "Tela Azul", "Ao Ligar o equipamento, no carregamento do windows 10, usuario informa que aparece tela azul sem a possibilidade de login",
									PrioridadeEnum.MEDIA, StatusEnum.ABERTO, "Cliente solicita breve prioridade no atendimento, cliente da empresa a 10 anos", C1, T1);
		chamadosRepository.saveAll(Arrays.asList(CH1));





	}
}
