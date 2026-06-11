package com.example.hub_entretenimento.domain;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Diretor")
public class Diretor {

    @Id
    private String nome; // Nome como identificador único

    public Diretor() {}

    public Diretor(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}