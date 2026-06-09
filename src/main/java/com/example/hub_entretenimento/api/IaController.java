package com.example.hub_entretenimento.api;

import com.example.hub_entretenimento.api.dto.RecomendacaoFilmeDTO;
import com.example.hub_entretenimento.service.EntretenimentoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entretenimento")
@CrossOrigin(origins = "http://localhost:4200") // <-- Libera o Angular local
public class IaController {

    private final EntretenimentoService entretenimentoService;

    // Injeção explícita via construtor do nosso serviço abstrato
    public IaController(EntretenimentoService entretenimentoService) {
        this.entretenimentoService = entretenimentoService;
    }

    @GetMapping("/recomendar")
    public RecomendacaoFilmeDTO obterRecomendacao(@RequestParam String gostoDoUsuario) {
        System.out.println("--> O Angular bateu no endpoint! Gosto recebido: " + gostoDoUsuario);

        return entretenimentoService.buscarRecomendacaoInteligente(gostoDoUsuario);
    }
}