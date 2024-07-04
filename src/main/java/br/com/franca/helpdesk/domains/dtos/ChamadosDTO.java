package br.com.franca.helpdesk.domains.dtos;

import br.com.franca.helpdesk.domains.Chamado;
import br.com.franca.helpdesk.domains.enums.PrioridadeEnum;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ChamadosDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss.SSS")
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss.SSS")
    private LocalDateTime dataFechamento;

    @NotNull(message = "O campo PRIORIDADE é requerido")
    private PrioridadeEnum prioridade;

    @NotNull(message = "O campo STATUS é requerido")
    private StatusEnum statusEnum;

    @NotNull(message = "O campo TITULO é requerido")
    private String titulo;

    @NotNull(message = "O campo OBSERVAÇÕES é requerido")
    private String observacoes;

    @NotNull(message = "O campo TECNICO é requerido")
    private TecnicoDTO tecnico;

    @NotNull(message = "O campo Descricao é requerido")
    private String descricaoChamado;

    @NotNull(message = "O campo CLIENTE é requerido")
    private ClienteDTO cliente;

    private String nomeTecnico;

    private String nomeCliente;

    public ChamadosDTO() {
        super();
    }

    public ChamadosDTO(Chamado obj) {
        this.id = obj.getId();
        this.dataAbertura = obj.getDataAbertura();
        this.dataFechamento = obj.getDataFechamento();
        this.prioridade = obj.getPrioridadeEnum();
        this.statusEnum = obj.getStatusEnum();
        this.titulo = obj.getTituloChamado();
        this.observacoes = obj.getObservacao();
        this.descricaoChamado = obj.getDescricaoChamado();
        this.nomeTecnico = obj.getTecnico().getNome();
        this.nomeCliente = obj.getCliente().getNome();
        this.tecnico = new TecnicoDTO(obj.getTecnico());
        this.cliente = new ClienteDTO(obj.getCliente());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public @NotNull(message = "O campo PRIORIDADE é requerido") PrioridadeEnum getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(@NotNull(message = "O campo PRIORIDADE é requerido") PrioridadeEnum prioridade) {
        this.prioridade = prioridade;
    }

    public @NotNull(message = "O campo STATUS é requerido") StatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(@NotNull(message = "O campo STATUS é requerido") StatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public @NotNull(message = "O campo TITULO é requerido") String getTitulo() {
        return titulo;
    }

    public void setTitulo(@NotNull(message = "O campo TITULO é requerido") String titulo) {
        this.titulo = titulo;
    }

    public @NotNull(message = "O campo OBSERVAÇÕES é requerido") String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(@NotNull(message = "O campo OBSERVAÇÕES é requerido") String observacoes) {
        this.observacoes = observacoes;
    }

    public @NotNull(message = "O campo Descricao é requerido") String getDescricaoChamado() {
        return descricaoChamado;
    }

    public void setDescricaoChamado(@NotNull(message = "O campo Descricao é requerido") String descricaoChamado) {
        this.descricaoChamado = descricaoChamado;
    }

    public String getNomeTecnico() {
        return nomeTecnico;
    }

    public void setNomeTecnico(String nomeTecnico) {
        this.nomeTecnico = nomeTecnico;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public @NotNull(message = "O campo TECNICO é requerido") TecnicoDTO getTecnico() {
        return tecnico;
    }

    public void setTecnico(@NotNull(message = "O campo TECNICO é requerido") TecnicoDTO tecnico) {
        this.tecnico = tecnico;
    }

    public @NotNull(message = "O campo CLIENTE é requerido") ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(@NotNull(message = "O campo CLIENTE é requerido") ClienteDTO cliente) {
        this.cliente = cliente;
    }
}