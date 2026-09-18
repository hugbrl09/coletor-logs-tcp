package com.mycompany.coletorlogsservidortcp;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class Requisicao {
    private String operacao;      // "REGISTRAR" OU "LISTAR"
    private String nivel;         // FIltro para consulta: "TODOS", "INFO", "WARN", "ERROR"
    private List<Evento> eventos; // Lista de eventos enviada na requisição de registro

    public Requisicao() {
        this.eventos = new ArrayList<>();
    }

    public Requisicao(String operacao, String nivel, List<Evento> eventos) {
        this.operacao = operacao;
        this.nivel = nivel;
        this.eventos = (eventos != null) ? eventos : new ArrayList<>();
    }
    
    public String paraLinha() {
        return new Gson().toJson(this);
    }
    
    public static Requisicao fromLinha(String json) {
        return new Gson().fromJson(json, Requisicao.class);
    }

    public String getOperacao() {
        return operacao;
    }

    public String getNivel() {
        return nivel;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }
}
