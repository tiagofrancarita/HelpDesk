package br.com.franca.HelpDesk.domains.enums;


public enum PrioridadeEnum {

    BAIXA(0,"BAIXA"), MEDIA(1, "MEDIA"), ALTA(2, "ALTA"), CRITICA(3, "CRITICA");

    private Integer codigo;
    private String descricao;

    private PrioridadeEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

public static PrioridadeEnum toEnum(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        for (PrioridadeEnum x : PrioridadeEnum.values()) {
            if (codigo.equals(x.getCodigo())) {
                return x;
            }
        }

        throw new IllegalArgumentException("Prioridade inválida: " + "código: " + codigo);
    }


}
