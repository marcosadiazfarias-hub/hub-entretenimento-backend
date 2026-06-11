package com.example.hub_entretenimento.config;

import com.example.hub_entretenimento.memory.Neo4jChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    // Substituímos a RAM (InMemory) pela injeção do nosso repositório do Neo4j customizado!
    @Bean
    public ChatMemory chatMemory(Neo4jChatMemoryRepository neo4jRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(neo4jRepository)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}