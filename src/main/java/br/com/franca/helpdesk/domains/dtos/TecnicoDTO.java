package br.com.franca.helpdesk.domains.dtos;

import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.domains.enums.Perfil;
import br.com.franca.helpdesk.util.PerfilDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ApiModel(description = "Detalhes do Técnico")
public class TecnicoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(notes = "ID do Técnico", example = "1")
    private Long id;

    @ApiModelProperty(notes = "Nome completo do tecnico", example = "Jõao da Silva")
    private String nome;

    @ApiModelProperty(notes = "Email do Técnico", example = "tecnico01@exemple.com")
    private String email;

    @ApiModelProperty(notes = "CPF do Técnico", example = "123.456.789-00")
    private String cpf;

    @ApiModelProperty(notes = "Senha do Técnico", example = "password123")
    private String senha;

    @ApiModelProperty(notes = "Data de Criação do Técnico", example = "2023-05-26T19:52:58")
    @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss.SSS")
    private LocalDateTime dataCriacao;

    @ApiModelProperty(notes = "Perfis do Técnico", example = "[\"ADMIN\", \"USER\"]")
    protected Set<Integer> perfis = new HashSet<>();

    public TecnicoDTO() {
        super();
    }

    // Getters e Setters
    // Construtor que aceita um objeto Tecnico
    public TecnicoDTO(Tecnico obj) {
        super();
        this.id = obj.getId();
        this.nome = obj.getNome();
        this.email = obj.getEmail();
        this.cpf = obj.getCpf();
        this.senha = obj.getSenha();
        this.dataCriacao = obj.getDataCriacao();
        this.perfis = obj.getPerfis().stream().map(x -> x.getCodigo()).collect(Collectors.toSet());

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Set<Perfil> getPerfis() {
        return perfis.stream().map(x -> Perfil.toEnum(x)).collect(Collectors.toSet());
    }

}