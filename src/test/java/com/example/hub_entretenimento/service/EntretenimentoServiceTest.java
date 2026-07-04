package com.example.hub_entretenimento.service;

import com.example.hub_entretenimento.api.dto.RecomendacaoFilmeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntretenimentoServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.PromptUserSpec promptUserSpec;

    @Mock
    private ChatClient.AdvisorSpec advisorSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private ChatMemory chatMemory;

    private EntretenimentoService entretenimentoService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.defaultAdvisors(any(org.springframework.ai.chat.client.advisor.api.Advisor[].class)))
                .thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(any(Consumer.class))).thenAnswer(invocation -> {
            Consumer<ChatClient.PromptUserSpec> consumer = invocation.getArgument(0);
            consumer.accept(promptUserSpec);
            return requestSpec;
        });
        when(requestSpec.advisors(any(Consumer.class))).thenAnswer(invocation -> {
            Consumer<ChatClient.AdvisorSpec> consumer = invocation.getArgument(0);
            consumer.accept(advisorSpec);
            return requestSpec;
        });
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(promptUserSpec.text(anyString())).thenReturn(promptUserSpec);
        when(promptUserSpec.param(anyString(), any())).thenReturn(promptUserSpec);
        when(advisorSpec.param(anyString(), any())).thenReturn(advisorSpec);

        entretenimentoService = new EntretenimentoService(chatClientBuilder, chatMemory);
    }

    @Test
    void buscarRecomendacaoInteligente_deveRetornarDTOMapeadoDaRespostaDaIA() {
        RecomendacaoFilmeDTO recomendacaoEsperada = new RecomendacaoFilmeDTO(
                "O Iluminado",
                1980,
                "Stanley Kubrick",
                "Um clássico de terror",
                List.of("Terror", "Suspense"),
                "Porque combina com o gosto informado",
                "sessao-123"
        );

        when(callResponseSpec.entity(RecomendacaoFilmeDTO.class)).thenReturn(recomendacaoEsperada);

        RecomendacaoFilmeDTO resultado = entretenimentoService.buscarRecomendacaoInteligente("terror dos anos 80", "sessao-123");

        assertEquals(recomendacaoEsperada, resultado);
        verify(promptUserSpec).param("gosto", "terror dos anos 80");
        verify(advisorSpec).param("chat_memory_conversation_id", "sessao-123");
    }

    @Test
    void buscarRecomendacaoInteligente_deveConstruirPromptComPreferenciaEHistoriaDaSessao() {
        when(callResponseSpec.entity(RecomendacaoFilmeDTO.class)).thenReturn(new RecomendacaoFilmeDTO(
                "A Vida é Bela",
                1997,
                "Roberto Benigni",
                "Uma história inspiradora",
                List.of("Drama", "Comédia"),
                "Boa recomendação",
                "sessao-456"
        ));
        when(promptUserSpec.text(anyString())).thenReturn(promptUserSpec);
        when(promptUserSpec.param(anyString(), any())).thenReturn(promptUserSpec);
        when(advisorSpec.param(anyString(), any())).thenReturn(advisorSpec);

        entretenimentoService.buscarRecomendacaoInteligente("drama", "sessao-456");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(promptUserSpec).text(promptCaptor.capture());
        assertEquals(true, promptCaptor.getValue().contains("Preferências do usuário"));
        assertEquals(true, promptCaptor.getValue().contains("{gosto}"));
        verify(promptUserSpec).param("gosto", "drama");
        verify(advisorSpec).param("chat_memory_conversation_id", "sessao-456");
    }
}
