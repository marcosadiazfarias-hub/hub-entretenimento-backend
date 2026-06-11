package com.example.hub_entretenimento.domain;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.ArrayList;
import java.util.List;

@Node("Filme")
public class Filme {

    @Id
    private String titulo;
    private int anoLancamento;
    private String sinopseCurta;

    // NOVO RELACIONAMENTO: Liga o filme ao seu nó Diretor
    @Relationship(type = "DIRIGIDO_POR", direction = Relationship.Direction.OUTGOING)
    private Diretor diretor;

    // NOVO RELACIONAMENTO: Liga o filme a múltiplos nós de Gênero
    @Relationship(type = "PERTENCE_AO", direction = Relationship.Direction.OUTGOING)
    private List<Genero> generos = new ArrayList<>();

    public Filme() {}

    public Filme(String titulo, int anoLancamento, String sinopseCurta) {
        this.titulo = titulo;
        this.anoLancamento = anoLancamento;
        this.sinopseCurta = sinopseCurta;
    }

    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(int anoLancamento) { this.anoLancamento = anoLancamento; }
    public String getSinopseCurta() { return sinopseCurta; }
    public void setSinopseCurta(String sinopseCurta) { this.sinopseCurta = sinopseCurta; }
    public Diretor getDiretor() { return diretor; }
    public void setDiretor(Diretor diretor) { this.diretor = diretor; }
    public List<Genero> getGeneros() { return generos; }
    public void setGeneros(List<Genero> generos) { this.generos = generos; }
}