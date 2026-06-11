package com.example.hub_entretenimento.domain;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Genero")
public class Genero {

    @Id
    private String nome; // Ex: "Ficção Científica", "Ação"

    public Genero() {}

    public Genero(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}