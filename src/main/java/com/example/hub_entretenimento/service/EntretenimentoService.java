package com.example.hub_entretenimento.service;

import com.example.hub_entretenimento.api.dto.RecomendacaoFilmeDTO;
import com.example.hub_entretenimento.domain.Diretor;
import com.example.hub_entretenimento.domain.Filme;
import com.example.hub_entretenimento.domain.Genero;
import com.example.hub_entretenimento.domain.MensagemHistorico;
import com.example.hub_entretenimento.repository.DiretorRepository;
import com.example.hub_entretenimento.repository.FilmeRepository;
import com.example.hub_entretenimento.repository.GeneroRepository;
import com.example.hub_entretenimento.repository.MensagemHistoricoRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EntretenimentoService {

    private final ChatClient chatClient;
    private final FilmeRepository filmeRepository;
    private final MensagemHistoricoRepository mensagemHistoricoRepository;
    private final DiretorRepository diretorRepository;
    private final GeneroRepository generoRepository;

    public EntretenimentoService(ChatClient.Builder builder, ChatMemory chatMemory,
                                 FilmeRepository filmeRepository,
                                 MensagemHistoricoRepository mensagemHistoricoRepository,
                                 DiretorRepository diretorRepository,
                                 GeneroRepository generoRepository) {
        this.chatClient = builder
                .defaultAdvisors(org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.filmeRepository = filmeRepository;
        this.mensagemHistoricoRepository = mensagemHistoricoRepository;
        this.diretorRepository = diretorRepository;
        this.generoRepository = generoRepository;
    }

    @Transactional
    public RecomendacaoFilmeDTO buscarRecomendacaoInteligente(String gostoDoUsuario, String sessionId) {

        // 1. Atualizamos o Prompt para forçar o Gemini a trazer múltiplas opções distintas
        RecomendacaoFilmeDTO dto = this.chatClient.prompt()
                .user(u -> u.text("""
                        Com base nas preferências do usuário, recomende uma lista contendo até 3 opções de filmes distintos que combinem com o perfil.
                        Considere rigorosamente o histórico da conversa para evitar repetir sugestões já feitas anteriormente.
                        
                        Preferências do usuário: "{gosto}"
                        """)
                        .param("gosto", gostoDoUsuario))
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", sessionId))
                .call()
                .entity(RecomendacaoFilmeDTO.class);

        // 2. Se o DTO veio preenchido e possui filmes na lista, processamos o lote no Grafo
        if (dto != null && dto.filmes() != null && !dto.filmes().isEmpty()) {

            // Instanciamos a mensagem do assistente que conectará a todos os filmes recomendados nesta rodada
            MensagemHistorico mensagemAssistant = new MensagemHistorico(
                    sessionId,
                    "ASSISTANT",
                    "Apresentei 3 opções de recomendações. Justificativa: " + dto.justificativaRecomendacao()
            );

            // Lista temporária para persistirmos os filmes que faremos relacionamento em lote
            List<Filme> filmesParaVincular = new ArrayList<>();

            for (RecomendacaoFilmeDTO.DetalheFilmeDTO filmeDto : dto.filmes()) {
                if (filmeDto.titulo() == null || filmeDto.titulo().isBlank()) continue;

                // 2.1 Processa ou recupera o nó do Diretor para este filme específico
                Diretor diretor = diretorRepository.findById(filmeDto.diretor())
                        .orElseGet(() -> diretorRepository.save(new Diretor(filmeDto.diretor())));

                // 2.2 Processa ou recupera os nós dos Gêneros para este filme específico
                List<Genero> generosDoGrafo = new ArrayList<>();
                if (filmeDto.generos() != null) {
                    for (String nomeGen : filmeDto.generos()) {
                        Genero g = generoRepository.findById(nomeGen)
                                .orElseGet(() -> generoRepository.save(new Genero(nomeGen)));
                        generosDoGrafo.add(g);
                    }
                }

                // 2.3 Monta ou recupera o objeto Filme conectando os nós de Diretor e Gênero
                Filme filme = filmeRepository.findById(filmeDto.titulo())
                        .orElseGet(() -> {
                            Filme novoFilme = new Filme(filmeDto.titulo(), filmeDto.anoLancamento(), filmeDto.sinopseCurta());
                            novoFilme.setDiretor(diretor);
                            novoFilme.setGeneros(generosDoGrafo);
                            return filmeRepository.save(novoFilme);
                        });

                filmesParaVincular.add(filme);
            }

            // O pulo do gato: Se você quiser manter o mapeamento de 1 para N na MensagemHistorico,
            // idealmente a sua classe MensagemHistorico deveria aceitar uma List<Filme> ou você pode
            // salvar múltiplos nós de histórico. Para mantermos o código simples e funcional sem mudar
            // a entidade MensagemHistorico agora, vamos registrar a primeira indicação diretamente nela
            // e salvar as outras como nós conectados na mesma sessão!

            if (!filmesParaVincular.isEmpty()) {
                // Vincula o primeiro filme ao nó de histórico principal
                mensagemAssistant.setFilmeRecomendado(filmesParaVincular.get(0));
                mensagemHistoricoRepository.save(mensagemAssistant);

                // Para os outros 2 filmes da lista, geramos nós extras de recomendação para o grafo não perder o vínculo
                for (int i = 1; i < filmesParaVincular.size(); i++) {
                    MensagemHistorico extra = new MensagemHistorico(
                            sessionId,
                            "ASSISTANT",
                            "Opção complementar recomendada: " + filmesParaVincular.get(i).getTitulo()
                    );
                    extra.setFilmeRecomendado(filmesParaVincular.get(i));
                    mensagemHistoricoRepository.save(extra);
                }
            }
        }

        return dto;
    }
}