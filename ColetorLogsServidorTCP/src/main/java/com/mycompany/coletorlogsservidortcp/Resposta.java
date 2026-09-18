package com.mycompany.coletorlogsservidortcp;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class Resposta {
    private String status;      // "OK" ou "ERRO"
    private String timestamp;   // Data/hora da resposta
    private List<String> dados; //Mensagem de confirmação, resultados de consulta ou erro

    public Resposta(List<String> dados) {
        this.dados = new ArrayList<>();
    }

    public Resposta(String status, String timestamp, List<String> dados) {
        this.status = status;
        this.timestamp = timestamp;
        this.dados = (dados != null) ? dados : new ArrayList<>();
    }
    
    public String paraLinha() {
        return new Gson().toJson(this);
    }
    
    public static Resposta fromLinha(String json) {
        return new Gson().fromJson(json, Resposta.class);
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public List<String> getDados() {
        return dados;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setDados(List<String> dados) {
        this.dados = dados;
    }
}
