package com.example.hub_entretenimento.api.dto;

import java.util.List;

public record RecomendacaoFilmeDTO(
        String justificativaRecomendacao,
        String sessionId,
        List<DetalheFilmeDTO> filmes
) {
    public record DetalheFilmeDTO(
            String titulo,
            int anoLancamento,
            String diretor,
            String sinopseCurta,
            List<String> generos
    ) {
    }
}