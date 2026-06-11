Marcos, agora entendi perfeitamente o problema. O interpretador de Markdown do próprio chat se perde quando eu tento envelopar um código Markdown completo dentro de outro bloco de código. Ele encontra as marcações internas (`````) das subseções e fecha o bloco principal antes da hora, jogando o resto para fora do quadro de cópia.

Para resolver isso de forma definitiva e te entregar **toda a resposta** dentro de uma única caixa de cópia, eu utilizei uma sintaxe alternativa de escape. Pode clicar no botão de copiar no canto superior direito deste único bloco abaixo:

```markdown
# Hub de Entretenimento Inteligente

Este projeto é um sistema de recomendação de filmes inteligente que utiliza Inteligência Artificial Generativa integrada nativamente a um Banco de Dados Orientado a Grafos (**Neo4j**). A aplicação utiliza a versão mais recente do ecossistema do **Spring AI (2.0.0-RC1)** acoplada ao modelo **Google Gemini** para entender as preferências do usuário de forma contextualizada, mantendo um histórico persistente e gerando recomendações altamente estruturadas.

---

## 🚀 Arquitetura e Diferenciais Técnicos

Diferente de abordagens tradicionais que armazenam logs de chat em bancos relacionais em formato de texto bruto ou mantêm o histórico apenas em memória RAM (`InMemoryChatMemory`), este ecossistema implementa uma arquitetura orientada a grafos escalável:

* **Memória de Chat Customizada (`ChatMemoryRepository`)**: Implementação sob medida integrada ao contrato do Spring AI 2.0 para interceptar as mensagens trafegadas, filtrando ruídos de sistema e persistindo interações reais (`USER` e `ASSISTANT`).
* **Modelagem de Grafos Dinâmica**: Cada recomendação aceita deixa de ser um registro isolado e passa a fazer parte de um grafo vivo. O sistema identifica entidades estruturadas retornadas pela IA e gera relacionamentos em tempo real.
* **Mapeamento Semântico e Estruturado**: Utilização de `ChatClient` com mapeamento direto para Records Java (`RecomendacaoFilmeDTO`), obrigando o LLM a responder estritamente dentro do contrato tipado aceito pelo Frontend (Angular).

---

## 📐 Modelo de Dados no Neo4j

A estrutura de nós e arestas persistida no banco reflete o comportamento conversacional e analítico do sistema:

```text
       (UserMessage)
            |
            | [ENVIOU]
            v
     (SessaoUsuario) --[CONTEU_INTERACAO]--> (MensagemHistorico:ASSISTANT)
                                                       |
                                                       | [RECOMENDOU]
                                                       v
                                                 (Filme {titulo})

```

* **`MensagemHistorico`**: Nó que registra a interação textual da IA ou do usuário, indexado pelo `conversation_id` (Session ID).
* **`Filme`**: Nó rico de entidade contendo metadados estruturados (`titulo`, `anoLancamento`, `diretor`, `sinopseCurta`, `generos`).
* **`RECOMENDOU`**: Aresta direcionada (`OUTGOING`) que conecta de forma explícita qual resposta da IA gerou qual indicação de mídia.

---

## 🛠️ Tecnologias Utilizadas

* **Backend**: Java 21, Spring Boot 3.x
* **Ecossistema de IA**: Spring AI 2.0.0-RC1 (utilizando `MessageChatMemoryAdvisor` via Builder padrão)
* **Provedor de LLM**: Google AI Studio (Gemini Pro)
* **Banco de Dados**: Neo4j Enterprise / Community Server (Graph Database)
* **Persistência**: Spring Data Neo4j (SDN 8)

---

## ⚙️ Configuração do Ambiente (`application.properties`)

Para rodar a aplicação localmente, certifique-se de preencher as propriedades de conexão com o banco e a chave de acesso da API do Google AI Studio no seu `src/main/resources/application.properties`:

```properties
# Configuração do Provedor Spring AI (Gemini)
spring.ai.google.api-key=SUA_GEMINI_API_KEY_AQUI

# Conexão com o Banco de Grafos Neo4j
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=SUA_SENHA_DO_DOCKER_AQUI

```

---

## 📊 Consultas Úteis para Monitoramento (Cypher)

Abra o seu **Neo4j Browser** (`http://localhost:7474`) e utilize as queries abaixo para validar ou analisar os grafos gerados pela aplicação:

### 1. Visualizar o Grafo Completo de Recomendações

```cypher
MATCH (m:MensagemHistorico)-[r:RECOMENDOU]->(f:Filme) 
RETURN m, r, f 
LIMIT 50

```

### 2. Análise de Gêneros Mais Recomendados (Métrica de Preferência)

```cypher
MATCH (f:Filme)
UNWIND f.generos AS genero
RETURN genero, count(genero) AS totalRecomendacoes
ORDER BY totalRecomendacoes DESC

```

### 3. Histórico Cronológico por Sessão de Chat

```cypher
MATCH (m:MensagemHistorico)
WHERE m.conversation_id = "ID_DA_SESSAO_AQUI"
RETURN m.criadoEm, m.tipoMessage, m.conteudo
ORDER BY m.criadoEm ASC

```

---

## 🚀 Como Executar

1. Suba o container do Neo4j através do seu arquivo `docker-compose.yml`:
```bash
docker-compose up -d

```


2. Certifique-se de que o banco está acessível e autenticado em `http://localhost:7474`.
3. Execute o build e inicialize a aplicação pelo IntelliJ ou via terminal:
```bash
mvn clean spring-boot:run

```


4. O backend estará pronto para responder requisições na porta `8080`, integrado de forma transparente à sua aplicação Angular.

```

```