package com.example.hub_entretenimento.service;

import com.example.hub_entretenimento.api.dto.RecomendacaoFilmeDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor; // <-- Voltamos para o Advisor principal
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class EntretenimentoService {

    private final ChatClient chatClient;

    public EntretenimentoService(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                // A SOLUÇÃO: Usamos o .builder() estático em vez do operador 'new'
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public RecomendacaoFilmeDTO buscarRecomendacaoInteligente(String gostoDoUsuario, String sessionId) {
        return this.chatClient.prompt()
                .user(u -> u.text("""
                        Com base nas preferências do usuário, recomende exatamente um filme ideal.
                        Considere o histórico da conversa para evitar repetir sugestões já feitas
                        e para entender pedidos de continuidade (ex: "quero outro parecido").
                        
                        Preferências do usuário: "{gosto}"
                        """)
                        .param("gosto", gostoDoUsuario))
                // Chave de texto literal para injetar o ID da sessão
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", sessionId))
                .call()
                .entity(RecomendacaoFilmeDTO.class);
    }
}