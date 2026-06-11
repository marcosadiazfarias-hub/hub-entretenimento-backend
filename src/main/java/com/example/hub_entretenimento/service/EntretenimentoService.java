package com.example.hub_entretenimento.service;

import com.example.hub_entretenimento.api.dto.RecomendacaoFilmeDTO;
import com.example.hub_entretenimento.domain.Filme;
import com.example.hub_entretenimento.domain.MensagemHistorico;
import com.example.hub_entretenimento.repository.FilmeRepository;
import com.example.hub_entretenimento.repository.MensagemHistoricoRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntretenimentoService {

    private final ChatClient chatClient;
    private final FilmeRepository filmeRepository;
    private final MensagemHistoricoRepository mensagemHistoricoRepository;

    // Construtor corrigido usando o Builder padrão do Spring AI 2.0-RC1
    public EntretenimentoService(ChatClient.Builder builder, ChatMemory chatMemory,
                                 FilmeRepository filmeRepository,
                                 MensagemHistoricoRepository mensagemHistoricoRepository) {
        this.chatClient = builder
                // CORRIGIDO: Agora usamos o construtor estático aceito pelo framework
                .defaultAdvisors(org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.filmeRepository = filmeRepository;
        this.mensagemHistoricoRepository = mensagemHistoricoRepository;
    }

    @Transactional // Garante consistência ao salvar no banco de dados
    public RecomendacaoFilmeDTO buscarRecomendacaoInteligente(String gostoDoUsuario, String sessionId) {

        // 1. Busca a recomendação estruturada do Gemini
        RecomendacaoFilmeDTO dto = this.chatClient.prompt()
                .user(u -> u.text("""
                        Com base nas preferências do usuário, recomende exatamente um filme ideal.
                        Considere o histórico da conversa para evitar repetir sugestões já feitas.
                        
                        Preferências do usuário: "{gosto}"
                        """)
                        .param("gosto", gostoDoUsuario))
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", sessionId))
                .call()
                .entity(RecomendacaoFilmeDTO.class);

        // 2. Se a resposta foi gerada com sucesso, criamos o vínculo no Grafo
        if (dto != null && dto.titulo() != null) {

            // Cria ou recupera o nó do Filme para evitar duplicados
            Filme filme = filmeRepository.findById(dto.titulo())
                    .orElseGet(() -> filmeRepository.save(new Filme(
                            dto.titulo(),
                            dto.anoLancamento(),
                            dto.diretor(),
                            dto.sinopseCurta(),
                            dto.generos()
                    )));

            // Cria uma entidade de histórico para a resposta do Assistente e vincula o Filme nela
            MensagemHistorico mensagemAssistant = new MensagemHistorico(
                    sessionId,
                    "ASSISTANT",
                    "Recomendei o filme: " + filme.getTitulo() + ". Justificativa: " + dto.justificativaRecomendacao()
            );
            mensagemAssistant.setFilmeRecomendado(filme);

            // Salva no Neo4j criando a aresta (MensagemHistorico)-[:RECOMENDOU]->(Filme)
            mensagemHistoricoRepository.save(mensagemAssistant);
        }

        return dto;
    }
}