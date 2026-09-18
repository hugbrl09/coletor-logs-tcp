package com.mycompany.coletorlogsclientetcp;

public class Evento {
    private String nivel;       // INFO, WARN ou ERROR
    private String origem;      // Ex: checkout-api
    private String mensagem;    // Ex: Pedido iniciado

    public Evento() {
    }

    public Evento(String nivel, String origem, String mensagem) {
        this.nivel = nivel;
        this.origem = origem;
        this.mensagem = mensagem;
    }

    public String getNivel() {
        return nivel;
    }

    public String getOrigem() {
        return origem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
