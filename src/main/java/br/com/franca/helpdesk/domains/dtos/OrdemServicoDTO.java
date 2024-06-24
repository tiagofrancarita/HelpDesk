package br.com.franca.helpdesk.domains.dtos;

import br.com.franca.helpdesk.domains.enums.StatusEnum;

import java.time.LocalDateTime;

public class OrdemServicoDTO {

    private Long id;
    private Long chamadoId;
    private String numeroChamado;
    private String descricao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataFechamento;
    private String problema;
    private String tratativa;
    private String solucao;
    private StatusEnum statusEnum;
    private Long tecnicoId;
    private String tecnicoNome;
    private Long clienteId;
    private String clienteNome;

    public OrdemServicoDTO(Long id, Long chamadoId, String numeroChamado, String descricao, LocalDateTime dataCriacao,
                           LocalDateTime dataFechamento, String problema, String tratativa, String solucao, StatusEnum statusEnum,
                           Long tecnicoId, String tecnicoNome, Long clienteId, String clienteNome) {
        this.id = id;
        this.chamadoId = chamadoId;
        this.numeroChamado = numeroChamado;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.dataFechamento = dataFechamento;
        this.problema = problema;
        this.tratativa = tratativa;
        this.solucao = solucao;
        this.statusEnum = statusEnum;
        this.tecnicoId = tecnicoId;
        this.tecnicoNome = tecnicoNome;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
    }

    public OrdemServicoDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChamadoId() {
        return chamadoId;
    }

    public void setChamadoId(Long chamadoId) {
        this.chamadoId = chamadoId;
    }

    public String getNumeroChamado() {
        return numeroChamado;
    }

    public void setNumeroChamado(String numeroChamado) {
        this.numeroChamado = numeroChamado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getTratativa() {
        return tratativa;
    }

    public void setTratativa(String tratativa) {
        this.tratativa = tratativa;
    }

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }

    public StatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(StatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public String getTecnicoNome() {
        return tecnicoNome;
    }

    public void setTecnicoNome(String tecnicoNome) {
        this.tecnicoNome = tecnicoNome;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }
}
