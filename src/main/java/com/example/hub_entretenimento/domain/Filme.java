package com.example.hub_entretenimento.domain;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import java.util.List;

@Node("Filme")
public class Filme {

    @Id
    private String titulo; // Usaremos o título como ID único para simplificar e evitar duplicados

    private int anoLancamento;
    private String diretor;
    private String sinopseCurta;
    private List<String> generos;

    public Filme() {}

    public Filme(String titulo, int anoLancamento, String diretor, String sinopseCurta, List<String> generos) {
        this.titulo = titulo;
        this.anoLancamento = anoLancamento;
        this.diretor = diretor;
        this.sinopseCurta = sinopseCurta;
        this.generos = generos;
    }

    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(int anoLancamento) { this.anoLancamento = anoLancamento; }
    public String getDiretor() { return diretor; }
    public void setDiretor(String diretor) { this.diretor = diretor; }
    public String getSinopseCurta() { return sinopseCurta; }
    public void setSinopseCurta(String sinopseCurta) { this.sinopseCurta = sinopseCurta; }
    public List<String> getGeneros() { return generos; }
    public void setGeneros(List<String> generos) { this.generos = generos; }
}