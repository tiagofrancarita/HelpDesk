package br.com.franca.HelpDesk.domains;


import br.com.franca.HelpDesk.domains.enums.PrioridadeEnum;
import br.com.franca.HelpDesk.domains.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.security.PrivateKey;
import java.time.LocalDateTime;

@Entity
@Table(name = "chamados")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chamado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_abertura", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_fechamento", nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime dataFechamento = LocalDateTime.now();

    @Column(name = "titulo_chamado", nullable = false, columnDefinition = "TIMESTAMP")
    private String tituloChamado;

    @Column(name = "descricao_chamado", nullable = false, columnDefinition = "TIMESTAMP")
    private String descricaoChamado;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade_chamado", nullable = false, columnDefinition = "TIMESTAMP")
    private PrioridadeEnum prioridadeEnum;

    @Column(name = "status_chamado", nullable = false, columnDefinition = "TIMESTAMP")
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum;

    @Column(name = "observacao_chamado", nullable = true, columnDefinition = "TIMESTAMP")
    private String observacao;

    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Tecnico tecnico;

    public Chamado() {
        super();
    }

    public Chamado(Long id, String tituloChamado, String descricaoChamado, PrioridadeEnum prioridadeEnum,
                   StatusEnum statusEnum, String observacao, Cliente cliente, Tecnico tecnico) {
        this.id = id;
        this.tituloChamado = tituloChamado;
        this.descricaoChamado = descricaoChamado;
        this.prioridadeEnum = prioridadeEnum;
        this.statusEnum = statusEnum;
        this.observacao = observacao;
        this.cliente = cliente;
        this.tecnico = tecnico;
    }
}