package com.example.hub_entretenimento.service;

import com.example.hub_entretenimento.api.dto.RecomendacaoFilmeDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class EntretenimentoService {

    private final ChatClient chatClient;

    public EntretenimentoService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public RecomendacaoFilmeDTO buscarRecomendacaoInteligente(String gostoDoUsuario) {
        return this.chatClient.prompt()
                .user(u -> u.text("""
                        Com base nas preferências do usuário, recomende exatamente um filme ideal.
                        Preferências do usuário: "{gosto}"
                        """)
                        .param("gosto", gostoDoUsuario))
                .call()
                .entity(RecomendacaoFilmeDTO.class);
    }
}