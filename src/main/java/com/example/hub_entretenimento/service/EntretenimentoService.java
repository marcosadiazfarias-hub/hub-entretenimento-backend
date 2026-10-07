package com.example.hub_entretenimento.service;

import com.example.hub_entretenimento.api.dto.RecomendacaoFilmeDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class EntretenimentoService {

    private final ChatClient chatClient;

    public EntretenimentoService(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public RecomendacaoFilmeDTO buscarRecomendacaoInteligente(String gostoDoUsuario, String sessionId) {
        return this.chatClient.prompt()
                .user(u -> u.text("""
                                Você é um especialista em cinema. Com base nas preferências do usuário, forneça EXATAMENTE 3 opções distintas de filmes no campo 'filmes'.
                        
                                Regras obrigatórias:
                                1. O campo 'filmes' DEVE conter impreterivelmente 3 itens diferentes.
                                2. Não repita títulos já recomendados anteriormente na conversa.
                                3. Preencha todos os campos de cada filme (titulo, anoLancamento, diretor, sinopseCurta, generos).
                                4. Forneça uma visão geral na 'justificativaRecomendacao' explicando o porquê da seleção das 3 opções.
                                
                                Preferências informadas pelo usuário: "{gosto}"
                        """)
                        .param("gosto", gostoDoUsuario))
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", sessionId))
                .call()
                .entity(RecomendacaoFilmeDTO.class);
    }
}