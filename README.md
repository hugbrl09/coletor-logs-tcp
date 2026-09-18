# 📋 Coletor de Logs Centralizado com Sockets TCP

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=openjdk)
![NetBeans](https://img.shields.io/badge/NetBeans-IDE-1B6AC6?style=for-the-badge&logo=apachenetbeans)
![TCP](https://img.shields.io/badge/Protocolo-TCP-blue?style=for-the-badge)
![License](https://img.shields.io/badge/Licen%C3%A7a-MIT-green?style=for-the-badge)

Aplicação cliente/servidor concorrente desenvolvida em **Java** para a disciplina de **Sistemas Paralelos e Distribuídos**. O sistema utiliza **Sockets TCP**, **Pool de Threads (`ExecutorService`)**, **Exclusão Mútua (`synchronized`)** e **Interface Gráfica Swing** para realizar a coleta e a consulta centralizada de logs de múltiplas aplicações.

---

## 🎯 Sobre o Projeto

O **Coletor de Logs Centralizado** permite que diversas aplicações clientes acumulem eventos de log localmente (nível, origem e mensagem) e os enviem em lote para um servidor central através de conexões confiáveis orientadas a fluxo (TCP) utilizando mensagens em formato JSON.

### 💡 Conceitos Teóricos e Práticos Aplicados
* **Comunicação Orientada a Conexão (TCP):** Troca de mensagens via `ServerSocket` e `Socket`, com streams de texto (`BufferedReader` e `PrintWriter`) e delimitador de mensagem por quebra de linha (`\n`).
* **Processamento Concorrente em Pool de Threads:** O servidor delega cada nova conexão recebida no `accept()` para um `ExecutorService` (pool fixo de 10 threads), permitindo que múltiplos clientes sejam atendidos em paralelo sem bloquear a porta do servidor.
* **Proteção de Região Crítica:** Para simular um processamento pesado, cada evento leva 1 segundo para ser gravado no repositório compartilhado. O acesso à lista central é protegido via bloco/método `synchronized` para evitar **Condição de Corrida (Race Condition)**.
* **Interface Não-Bloqueante (SwingWorker):** O cliente Swing despacha a requisição de rede em segundo plano, evitando o congelamento da interface gráfica durante o envio dos pacotes.

---

## ⚙️ Arquitetura do Protocolo JSON

A comunicação entre Cliente e Servidor ocorre via objetos serializados/desserializados com a biblioteca **Google Gson**:

### ✉️ `Requisicao.java`
* **`operacao`**: `"REGISTRAR"` ou `"LISTAR"`.
* **`nivel`**: Filtro para consultas (`"TODOS"`, `"INFO"`, `"WARN"`, `"ERROR"`).
* **`eventos`**: Lista de objetos `Evento` enviados durante o registro.

### 📩 `Resposta.java`
* **`status`**: `"OK"` ou `"ERRO"`.
* **`timestamp`**: Data e hora do servidor no momento do processamento.
* **`dados`**: Lista de textos contendo confirmações de processamento ou os registros retornados pela consulta.

---

## 📁 Estrutura do Repositório

```text
coletor-logs-tcp/
├── ColetorLogsServidorTCP/
│   ├── src/main/java/com/mycompany/coletorlogsservidortcp/
│   │   ├── Evento.java
│   │   ├── Requisicao.java
│   │   ├── Resposta.java
│   │   ├── RepositorioLogs.java    # Região Crítica (synchronized)
│   │   └── ServidorApp.java        # ServerSocket (porta 9999) + ThreadPool
│   └── pom.xml
│
└── ColetorLogsClienteTCP/
    ├── src/main/java/com/mycompany/coletorlogsclientetcp/
    │   ├── Evento.java
    │   ├── Requisicao.java
    │   ├── Resposta.java
    │   ├── ServicoCliente.java     # Comunicação Socket TCP
    │   └── TelaCliente.java        # Interface Swing + SwingWorker
    └── pom.xml
```

---

## 🚀 Como Executar no NetBeans IDE

### 1. Clonar o Repositório
```bash
git clone https://github.com/hugbrl09/coletor-logs-tcp.git
cd coletor-logs-tcp
```

### 2. Abrir os Projetos
1. Abra o **NetBeans IDE**.
2. Vá em **File > Open Project...** (`Ctrl + Shift + O`).
3. Selecione as duas pastas: **`ColetorLogsServidorTCP`** e **`ColetorLogsClienteTCP`**.

### 3. Iniciar o Servidor
1. Expanda o projeto `ColetorLogsServidorTCP`.
2. Clique com o botão direito em **`ServidorApp.java`** e selecione **Run File** (`Shift + F6`).
3. Confirme a mensagem na aba *Output*:
   > `Servidor Coletor de Logs TCP ativo na porta 9999`

### 4. Iniciar o Cliente (Interface Gráfica)
1. Expanda o projeto `ColetorLogsClienteTCP`.
2. Clique com o botão direito em **`TelaCliente.java`** e selecione **Run File** (`Shift + F6`).

---

## 🧪 Teste de Concorrência (Intercalação de Logs)

Para comprovar a exclusão mútua e o atendimento paralelo no servidor:

1. Abra **duas janelas do cliente** executando o `Run File` na `TelaCliente.java` duas vezes.
2. Na **Janela 1**, monte 3 eventos (ex: `Origem: API-A` com mensagens `A1`, `A2`, `A3`).
3. Na **Janela 2**, monte 3 eventos (ex: `Origem: API-B` com mensagens `B1`, `B2`, `B3`).
4. Clique em **"Enviar Fila ao Servidor"** nas duas janelas simultaneamente.
5. Em qualquer janela, selecione o filtro `"TODOS"` e clique em **"Consultar Servidor"**.

### 📊 Exemplo do Resultado Esperado
Como cada evento leva 1 segundo para ser processado pela respectiva thread, as mensagens das duas instâncias aparecem **intercaladas no repositório central**:

```text
=== CONSULTA DE LOGS (TODOS) ===
Data/Hora: 2026-09-18T16:01:04.942247100 | Status: OK
----------------------------------------------------------------
[2026-09-18 16:00:59][INFO][API-A][A1][127.0.0.1:53209]
[2026-09-18 16:00:59][INFO][API-B][B1][127.0.0.1:53210]
[2026-09-18 16:01:00][INFO][API-A][A2][127.0.0.1:53209]
[2026-09-18 16:01:00][INFO][API-B][B2][127.0.0.1:53210]
[2026-09-18 16:01:01][INFO][API-A][A3][127.0.0.1:53209]
[2026-09-18 16:01:01][INFO][API-B][B3][127.0.0.1:53210]
```

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 17
* **IDE:** NetBeans IDE
* **Comunicação de Rede:** Sockets TCP (`java.net.ServerSocket`, `java.net.Socket`)
* **Concorrência:** `ExecutorService` (FixedThreadPool), `SwingWorker`, `synchronized`
* **Interface Gráfica:** Java Swing
* **Serialização:** Google Gson 2.10.1