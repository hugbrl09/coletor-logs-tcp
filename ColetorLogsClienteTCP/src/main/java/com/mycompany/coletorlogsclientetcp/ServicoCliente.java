package com.mycompany.coletorlogsclientetcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServicoCliente {
    private static final String HOST = "localhost";
    private static final int PORTA = 9999;
    
    public Resposta enviar(Requisicao req) throws Exception {
        // Abre a conexão TCP com o servidor, prepara o envio e a recepção de texto
        try (
            Socket socket = new Socket(HOST, PORTA);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // 1. Envia a linha JSON com a requisição
            saida.println(req.paraLinha());
            
            // 2. Aguarda e lê a linha JSON de resposta devolvida pelo servidor
            String jsonResp = entrada.readLine();
            
            if (jsonResp != null && !jsonResp.isEmpty()) {
                return Resposta.fromLinha(jsonResp);
            } else {
                throw new Exception("Nenhuma resposta recebida do servidor.");
            }
        }
    }
}
