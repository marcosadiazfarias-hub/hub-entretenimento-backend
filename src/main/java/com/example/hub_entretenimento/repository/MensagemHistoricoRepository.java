package com.example.hub_entretenimento.repository;

import com.example.hub_entretenimento.domain.MensagemHistorico;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MensagemHistoricoRepository extends Neo4jRepository<MensagemHistorico, Long> {

    // Mudamos o nome aqui para evitar 100% de conflito com o framework
    @Query("MATCH (m:MensagemHistorico {conversation_id: $conversationId}) RETURN m ORDER BY m.criadoEm ASC")
    List<MensagemHistorico> buscarMensagensPorSessao(String conversationId);
}