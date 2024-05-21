package br.com.franca.HelpDesk.domains;

import br.com.franca.HelpDesk.domains.enums.PerfilEnum;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Clientes")
@PrimaryKeyJoinColumn(name = "id")
@Data
public class Cliente extends Pessoa {

    @OneToMany(mappedBy = "cliente")
    private List<Chamado> chamados = new ArrayList<>();

    public Cliente() {
        super();
        addPerfil(PerfilEnum.CLIENTE);
    }

    public Cliente(Long id, String nome, String cpf, String email, String senha) {
        super(id, nome, cpf, email, senha);
        addPerfil(PerfilEnum.CLIENTE);
    }
}