package com.example.hub_entretenimento.api;

import com.example.hub_entretenimento.api.dto.RecomendacaoFilmeDTO;
import com.example.hub_entretenimento.service.EntretenimentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IaControllerTest {

    @Mock
    private EntretenimentoService entretenimentoService;

    private IaController iaController;

    @BeforeEach
    void setUp() {
        iaController = new IaController(entretenimentoService);
    }

    @Test
    void obterRecomendacao_deveRetornarRecomendacaoComSucesso() {
        // Arrange
        String gostoUsuario = "filmes de terror dos anos 80";
        RecomendacaoFilmeDTO recomendacaoEsperada = new RecomendacaoFilmeDTO(
                "O Iluminado",
                1980,
                "Stanley Kubrick",
                "Uma família vai para um hotel isolado e as coisas ficam assustadoras.",
                List.of("Terror", "Suspense"),
                "Porque é um clássico de terror dos anos 80.",
                "sessao-padrao"
        );

        when(entretenimentoService.buscarRecomendacaoInteligente(gostoUsuario, "sessao-padrao"))
                .thenReturn(recomendacaoEsperada);

        // Act
        RecomendacaoFilmeDTO resultado = iaController.obterRecomendacao(gostoUsuario, "sessao-padrao");

        // Assert
        assertEquals(recomendacaoEsperada, resultado);
        assertEquals("O Iluminado", resultado.titulo());
        assertEquals(1980, resultado.anoLancamento());
        
        // Verifica se o serviço foi chamado corretamente com o parâmetro recebido
        verify(entretenimentoService, times(1)).buscarRecomendacaoInteligente(gostoUsuario, "sessao-padrao");
    }
}