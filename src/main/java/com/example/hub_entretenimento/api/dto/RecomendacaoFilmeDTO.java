package com.example.hub_entretenimento.api.dto;

import java.util.List;

public record RecomendacaoFilmeDTO(
        String justificativaRecomendacao,
        List<DetalheFilmeDTO> filmes
) {
    // Record auxiliar para encapsular os dados de cada filme da lista
    public record DetalheFilmeDTO(
            String titulo,
            int anoLancamento,
            String diretor,
            String sinopseCurta,
            List<String> generos
    ) {}
}