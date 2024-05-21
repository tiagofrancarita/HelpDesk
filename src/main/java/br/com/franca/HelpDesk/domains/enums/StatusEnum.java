package br.com.franca.HelpDesk.domains.enums;


public enum StatusEnum {

    ABERTO(0,"ABERTO"), EXECUÇÃO(1, "EXECUÇÃO"), ENCERRADO(2, "ENCERRADO"), CANCELADO(3, "CANCELADO"), DEVOLVIDO(4, "DEVOLVIDO");

    private Integer codigo;
    private String descricao;

    private StatusEnum(Integer codigo, String descricao) {
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

public static StatusEnum toEnum(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        for (StatusEnum x : StatusEnum.values()) {
            if (codigo.equals(x.getCodigo())) {
                return x;
            }
        }

        throw new IllegalArgumentException("Status inválido: " + "código: " + codigo);
    }


}
