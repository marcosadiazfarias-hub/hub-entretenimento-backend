package com.example.hub_entretenimento.api.dto;

import java.util.List;

public record RecomendacaoFilmeDTO(
        String titulo,
        int anoLancamento,
        String diretor,
        String sinopseCurta,
        List<String> generos,
        String justificativaRecomendacao,
        String sessionId // <-- Adicionado para manter o rastreamento no Frontend
) {}