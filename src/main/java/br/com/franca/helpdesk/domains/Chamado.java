package br.com.franca.helpdesk.domains;


import br.com.franca.helpdesk.domains.dtos.ChamadosDTO;
import br.com.franca.helpdesk.domains.enums.PrioridadeEnum;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Chamado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private Long id;

    @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss.SSS")
    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss.SSS")
    @Column(name = "data_fechamento", nullable = true)
    private LocalDateTime dataFechamento;

    @Column(name = "titulo_chamado", nullable = false)
    private String tituloChamado;

    @Column(name = "descricao_chamado", nullable = false)
    private String descricaoChamado;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade_chamado", nullable = false)
    private @NotNull(message = "O campo PRIORIDADE é requerido") PrioridadeEnum prioridadeEnum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_chamado", nullable = false)
    private StatusEnum statusEnum = StatusEnum.ABERTO;

    @Column(name = "observacao_chamado", nullable = true)
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;

    public Chamado() {
        super();
    }

    public Chamado(Long id, String tituloChamado, String descricaoChamado, PrioridadeEnum prioridadeEnum, StatusEnum statusEnum, String observacao, Cliente cliente, Tecnico tecnico) {
        this.id = id;
        this.tituloChamado = tituloChamado;
        this.descricaoChamado = descricaoChamado;
        this.prioridadeEnum = prioridadeEnum;
        this.statusEnum = statusEnum;
        this.observacao = observacao;
        this.cliente = cliente;
        this.tecnico = tecnico;
    }

    public Chamado(ChamadosDTO chamadosDTO) {
        this.id = chamadosDTO.getId();
        this.tituloChamado = chamadosDTO.getTitulo();
        this.descricaoChamado = chamadosDTO.getDescricaoChamado();
        this.prioridadeEnum = chamadosDTO.getPrioridade();
        this.statusEnum = chamadosDTO.getStatusEnum();
        this.observacao = chamadosDTO.getObservacoes();
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

    public String getTituloChamado() {
        return tituloChamado;
    }

    public void setTituloChamado(String tituloChamado) {
        this.tituloChamado = tituloChamado;
    }

    public String getDescricaoChamado() {
        return descricaoChamado;
    }

    public void setDescricaoChamado(String descricaoChamado) {
        this.descricaoChamado = descricaoChamado;
    }

    public @NotNull(message = "O campo PRIORIDADE é requerido") PrioridadeEnum getPrioridadeEnum() {
        return prioridadeEnum;
    }

    public void setPrioridadeEnum(@NotNull(message = "O campo PRIORIDADE é requerido") PrioridadeEnum prioridadeEnum) {
        this.prioridadeEnum = prioridadeEnum;
    }

    public StatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(StatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }
}