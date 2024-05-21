package br.com.franca.helpdesk.domains;


import br.com.franca.helpdesk.domains.enums.PrioridadeEnum;
import br.com.franca.helpdesk.domains.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
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
    private PrioridadeEnum prioridadeEnum;

    @Column(name = "status_chamado", nullable = false)
    @Enumerated(EnumType.STRING)
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

    public PrioridadeEnum getPrioridadeEnum() {
        return prioridadeEnum;
    }

    public void setPrioridadeEnum(PrioridadeEnum prioridadeEnum) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Chamado other = (Chamado) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
