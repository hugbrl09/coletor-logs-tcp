package com.mycompany.coletorlogsservidortcp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RepositorioLogs {
    private final List<String> logs = new ArrayList<>();
    
    public synchronized void adicionar(Evento evento, String remetente) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        String linhaLog = String.format("[%s][%s][%s][%s][%s]",
                timestamp,
                evento.getNivel(),
                evento.getOrigem(),
                evento.getMensagem(),
                remetente
        );
        
        logs.add(linhaLog);
    }
    
    public synchronized List<String> listaPorNivel(String filtro) {
        List<String> resultado = new ArrayList<>();
        
        for (String log : logs) {
            if ("TODOS".equalsIgnoreCase(filtro) || log.contains("[" + filtro + "]")) {
                resultado.add(log);
            }
        }
        
        return resultado;
    }
}
