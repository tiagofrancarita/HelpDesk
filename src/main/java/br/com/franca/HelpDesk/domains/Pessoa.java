package br.com.franca.HelpDesk.domains;

import br.com.franca.HelpDesk.domains.enums.PerfilEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Size(min = 5, message = "Favor informar o nome completo")
    @NotBlank(message = "O campo nome deve ser informado!")
    @NotNull(message = "O campo nome deve ser informado!")
    @Column(name = "nome_completo", nullable = false)
    private String nome;

    @NotNull(message = "Favor informar o campo status da conta a pagar")
    @NotBlank(message = "O campo CPF deve ser informado!")
    @CPF(message = "CPF inválido")
    @Column(name = "cpf", nullable = false)
    private String cpf;

    @Email(message = "Campo e-mail é obrigatório")
    @Column(name = "email", nullable = false)
    private String email;


    @NotNull(message = "Favor informar o campo senha")
    @Size(min = 8, message = "O campo senha deve ter no mínimo 8 caracteres")
    @NotBlank(message = "O campo senha deve ser informado!")
    @Column(name = "senha", nullable = false)
    private String senha;

    @NotNull(message = "Favor informar o perfil do usuário")
    @Enumerated(EnumType.STRING)
    @Column(name = "status_conta_pagar", nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Integer> perfis = new HashSet<>();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_contratacao", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public Pessoa() {
        super();
        addPerfil(PerfilEnum.CLIENTE);
    }

    public Pessoa(Long id, String nome, String cpf, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        addPerfil(PerfilEnum.CLIENTE);
    }

    public Set<PerfilEnum> getPerfis() {
       return perfis.stream().map(x -> PerfilEnum.toEnum(x)).collect(Collectors.toSet());
    }

    public void addPerfil(PerfilEnum perfilEnum) {
        this.perfis.add(perfilEnum.getCodigo());
    }
}