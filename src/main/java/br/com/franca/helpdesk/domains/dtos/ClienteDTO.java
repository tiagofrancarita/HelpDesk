package br.com.franca.helpdesk.domains.dtos;

import br.com.franca.helpdesk.domains.Cliente;
import br.com.franca.helpdesk.domains.enums.Perfil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ApiModel(description = "Detalhes do Cliente")
public class ClienteDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(notes = "ID do Cliente", example = "1")
        private Long id;

        @ApiModelProperty(notes = "Nome completo do cliente", example = "Jõao da Silva")
        @NotNull(message = "O campo nome é obrigatório")
        @NotEmpty(message = "O campo nome é obrigatório")
        private String nome;

        @ApiModelProperty(notes = "Email do cliente", example = "tecnico01@exemple.com")
        @NotNull(message = "O campo email é obrigatório")
        @NotEmpty(message = "O campo email é obrigatório")
        private String email;

        @ApiModelProperty(notes = "CPF do cliente", example = "123.456.789-00")
        @NotNull(message = "O campo cpf é obrigatório")
        @NotEmpty(message = "O campo cpf é obrigatório")
        private String cpf;

        @ApiModelProperty(notes = "Senha do cliente", example = "password123")
        @NotNull(message = "O campo senha é obrigatório")
        @NotEmpty(message = "O campo senha é obrigatório")
        private String senha;

        @ApiModelProperty(notes = "Data de Criação do cliente", example = "2023-05-26T19:52:58")
        @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss.SSS")
        private LocalDateTime dataCriacao;

        @ApiModelProperty(notes = "Perfis do cliente", example = "[\"ADMIN\", \"USER\"]")
        @NotNull(message = "O campo perfil é obrigatório")
        @NotEmpty(message = "O campo perfil é obrigatório")
        private Set<Integer> perfis = new HashSet<>();


    public ClienteDTO(Cliente obj) {
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
