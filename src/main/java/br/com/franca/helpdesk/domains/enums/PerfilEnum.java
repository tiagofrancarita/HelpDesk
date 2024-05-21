package br.com.franca.helpdesk.domains.enums;




public enum PerfilEnum {

    ADMIN(0,"ROLE_ADMIN"), CLIENTE(1, "ROLE_CLIENTE"), TECNICO(2, "ROLE_TECNICO");

    private Integer codigo;
    private String descricao;

    private PerfilEnum(Integer codigo, String descricao) {
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

    public static PerfilEnum toEnum(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        for (PerfilEnum x : PerfilEnum.values()) {
            if (codigo.equals(x.getCodigo())) {
                return x;
            }
        }

        throw new IllegalArgumentException("Perfil inválido: " + "código: " + codigo);
    }
}