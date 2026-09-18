package com.mycompany.coletorlogsservidortcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorApp {
    private static final int PORTA = 9999;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final RepositorioLogs repositorio = new RepositorioLogs();
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor Coletor de Logs TCP ativo na porta " + PORTA);
            
            while (true) {
                // 1. Aguarda a conexão de um cliente
                Socket socketCliente = serverSocket.accept();
                
                // 2. Entrega o cliente para uma thread do pool
                pool.execute(() -> tratarCliente(socketCliente));
            }
        } catch (Exception e) {
            System.err.println("Erro no servidor TCP: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void tratarCliente(Socket socket) {
        String remetente = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        
        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // 3. Lê a linha JSON enviada pelo cliente
            String jsonReq = entrada.readLine();
            
            if (jsonReq != null && !jsonReq.isEmpty()){
                Requisicao req = Requisicao.fromLinha(jsonReq);
                Resposta resp;
                
                if ("REGISTRAR".equalsIgnoreCase(req.getOperacao())) {
                    int cont = 0;
                    for (Evento ev : req.getEventos()) {
                        repositorio.adicionar(ev, remetente);
                        cont++;
                        // Simula processamento pesado de 1s por evento
                        Thread.sleep(1000);
                    }
                    resp = new Resposta("OK", java.time.LocalDateTime.now().toString(),
                            java.util.List.of("Processados e armazenados " + cont + " evento(s)."));
                
                } else if ("LISTAR".equalsIgnoreCase(req.getOperacao())) {
                    java.util.List<String> logs = repositorio.listaPorNivel(req.getNivel());
                    resp = new Resposta("OK", java.time.LocalDateTime.now().toString(), logs);
                } else {
                    resp = new Resposta("ERRO", java.time.LocalDateTime.now().toString(),
                            java.util.List.of("Operação inválida."));
                }
                
                // 4. Envia o JSON de resposta
                saida.println(resp.paraLinha());
            }  
        } catch (Exception e) {
            System.err.println("Erro ao tratar cliente " + remetente + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {}
        }
    }
}
