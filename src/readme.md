Hub de Entretenimento Inteligente
Este projeto é um sistema inteligente de recomendação cinematográfica baseado em Inteligência Artificial Generativa
integrada ao ecossistema Spring AI e a um Banco de Dados Orientado a Grafos (Neo4j). A aplicação utiliza modelos
generativos (Google Gemini) para analisar preferências em linguagem natural e sugerir catálogos estruturados com suporte
a memória conversacional persistente e exibição em múltiplos cards no frontend (Angular).
Arquitetura e Diferenciais Técnicos
Suporte a Recomendações Multi-opções: Mapeamento do contrato estruturado com o LLM para retornar lotes de sugestões
(List<DetalheFilmeDTO>) juntamente com uma justificativa global contextualizada.
Memória Conversacional Integrada: Utilização de MessageChatMemoryAdvisor via Spring AI acoplado a identificadores de
sessão (X-Session-Id), garantindo que o modelo considere o histórico prévio para não sugerir mídias repetidas.
Modelagem em Grafo (Neo4j): Os registros de conversas e entidades de filmes, gêneros e diretores são modelados em nós e
arestas direcionadas, viabilizando análises de relacionamentos e preferências por grafos.
Tipagem Estrita Frontend-Backend: O ChatClient força a resposta do modelo diretamente no formato de Records do Java,
compatível com as interfaces TypeScript do Angular.
Modelo de Dados no Neo4j
A estrutura de nós e arestas reflete as interações conversacionais e os metadados de filmes recomendados:
(MensagemHistorico:ASSISTANT) -[RECOMENDOU]-> (Filme {titulo}) -[DIRIGIDO_POR]-> (Diretor {nome})

(Filme) -[PERTENCE_AO]-> (Genero {nome})

MensagemHistorico: Registra cada interação da conversa, indexada por conversation_id (sessionId).
Filme: Entidade com propriedades como título, ano de lançamento e sinopse curta.
Diretor / Genero: Nós independentes conectados aos filmes para permitir cruzamentos semânticos no grafo.
RECOMENDOU: Aresta direcionada (OUTGOING) que conecta a mensagem do assistente às mídias sugeridas.
Tecnologias Utilizadas
Backend: Java 21, Spring Boot 3.x
Ecossistema de IA: Spring AI (com MessageChatMemoryAdvisor e ChatClient)
Provedor de LLM: Google Gemini (gemini-3.1-flash-lite / Gemini Pro)
Banco de Dados de Grafos: Neo4j (via Spring Data Neo4j)
Frontend: Angular (Standalone Components, CSS Grid responsivo, controle reativo com ngModel e @for)
Configuração do Ambiente (application.yml)
Configure as propriedades da aplicação no arquivo src/main/resources/application.yml:spring:

application:

    name: hub-entretenimento

ai:

    google:

      genai:

        api-key: ${GEMINI_API_KEY}

        chat:

          options:

            model: gemini-3.1-flash-lite

            temperature: 0.4

neo4j:

    uri: bolt://localhost:7687

    authentication:

      username: neo4j

      password: SUA_SENHA_DO_NEO4J_AQUI

Configuração da Variável de Ambiente
A chave de API da Google deve ser fornecida através da variável GEMINI_API_KEY:

IntelliJ IDEA: Menu Run > Edit Configurations... > Adicione em Environment variables: GEMINI_API_KEY=sua_chave_aqui.
VS Code (launch.json): Adicione "GEMINI_API_KEY": "sua_chave_aqui" dentro de "env".
Terminal (PowerShell): $env:GEMINI_API_KEY="sua_chave_aqui"
Terminal (Bash / macOS / Linux): export GEMINI_API_KEY="sua_chave_aqui"
Consultas Úteis no Neo4j Browser (Cypher)
Acesse http://localhost:7474 para inspecionar os grafos persistidos:

Visualizar recomendações e filmes conectados:

MATCH (m:MensagemHistorico)-[r:RECOMENDOU]->(f:Filme)

RETURN m, r, f

LIMIT 50;

Cruzamento de diretores e gêneros dos filmes recomendados:

MATCH (f:Filme)-[:DIRIGIDO_POR]->(d:Diretor), (f)-[:PERTENCE_AO]->(g:Genero)

RETURN f.titulo, d.nome, collect (g.nome) AS generos;

Histórico cronológico de uma sessão de chat:

MATCH (m:MensagemHistorico)

WHERE m.conversation_id = "ID_DA_SESSAO_AQUI"

RETURN m.criadoEm, m.tipoMessage, m.conteudo

ORDER BY m.criadoEm ASC;
Como Executar
Subir a instância do Neo4j:docker-compose up -d
Compilar e rodar o Backend:mvn clean spring-boot:run
Subir o Frontend:ng serve

Acesse a interface em http://localhost:4200.
