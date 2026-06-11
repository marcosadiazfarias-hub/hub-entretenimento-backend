package com.example.hub_entretenimento.domain;

import org.springframework.data.neo4j.core.schema.*;

import java.time.Instant;

@Node("MensagemHistorico")
public class MensagemHistorico {

    @Id @GeneratedValue
    private Long id;

    @Property("conversation_id")
    private String conversationId;

    private String tipoMessage; // USER ou ASSISTANT

    private String conteudo;

    private Instant criadoEm;

    @Relationship(type = "RECOMENDOU", direction = Relationship.Direction.OUTGOING)
    private Filme filmeRecomendado;

    // Construtor padrão necessário para o Spring Data Neo4j
    public MensagemHistorico() {}

    public MensagemHistorico(String conversationId, String tipoMessage, String conteudo) {
        this.conversationId = conversationId;
        this.tipoMessage = tipoMessage;
        this.conteudo = conteudo;
        this.criadoEm = Instant.now();
    }

    // Getters e Setters simples (para evitar problemas de proxy com Records no Spring Data)
    public Long getId() { return id; }
    public String getConversationId() { return conversationId; }
    public String getTipoMessage() { return tipoMessage; }
    public String getConteudo() { return conteudo; }
    public Instant getCriadoEm() { return criadoEm; }
    public Filme getFilmeRecomendado() { return filmeRecomendado; }
    public void setFilmeRecomendado(Filme filmeRecomendado) { this.filmeRecomendado = filmeRecomendado; }
}