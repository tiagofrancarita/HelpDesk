package br.com.franca.HelpDesk.domains;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tecnicos")
@PrimaryKeyJoinColumn(name = "id")
@Data
public class Tecnico extends Pessoa {

    private List<Chamado> chamados = new ArrayList<>();

    public Tecnico() {
        super();
    }

    public Tecnico(Long id, String nome, String cpf, String email, String senha) {
        super(id, nome, cpf, email, senha);
    }
}