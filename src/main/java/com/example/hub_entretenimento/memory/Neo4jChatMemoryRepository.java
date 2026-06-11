package com.example.hub_entretenimento.memory;

import com.example.hub_entretenimento.domain.MensagemHistorico;
import com.example.hub_entretenimento.repository.MensagemHistoricoRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Neo4jChatMemoryRepository implements ChatMemoryRepository {

    private final MensagemHistoricoRepository repo;

    public Neo4jChatMemoryRepository(MensagemHistoricoRepository repo) {
        this.repo = repo;
    }

    // Método obrigatório do Spring AI 2.0 para recuperar o histórico
    @Override
    public List<Message> findByConversationId(String conversationId) {
        return repo.buscarMensagensPorSessao(conversationId).stream()
                .map(dbMsg -> {
                    if (MessageType.ASSISTANT.name().equalsIgnoreCase(dbMsg.getTipoMessage())) {
                        return new AssistantMessage(dbMsg.getConteudo());
                    } else {
                        return new UserMessage(dbMsg.getConteudo());
                    }
                })
                .collect(Collectors.toList());
    }

    // Método obrigatório do Spring AI 2.0 para salvar novas interações
    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        for (Message msg : messages) {
            if (msg.getText() != null && !msg.getText().isBlank()) {
                MensagemHistorico historico = new MensagemHistorico(
                        conversationId,
                        msg.getMessageType().name(),
                        msg.getText()
                );
                repo.save(historico);
            }
        }
    }

    @Override
    public List<String> findConversationIds() {
        return List.of(); // Pode ficar assim por enquanto
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        // Implementação futura opcional caso queira limpar chats da tela
    }
}