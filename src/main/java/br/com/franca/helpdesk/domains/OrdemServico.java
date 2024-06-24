package br.com.franca.helpdesk.domains;

import br.com.franca.helpdesk.domains.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordem_servico")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrdemServico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "chamado_id",nullable = false)
    private Chamado chamado;

    @Column(name = "numero_chamado",unique = true)
    private String numeroChamado;
    
    @Column(name = "descricao", nullable = false)
    private String descricao;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    @Column(name = "problema")
    private String problema;
    
    @Column(name = "tratativa")
    private String tratativa;

    @Column(name = "solucao")
    private String solucao;

    @Column(name = "status_chamado", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEnum statusEnum = StatusEnum.ABERTO;


}
