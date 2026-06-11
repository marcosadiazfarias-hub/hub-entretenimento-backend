package com.example.hub_entretenimento.repository;

import com.example.hub_entretenimento.domain.Filme;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmeRepository extends Neo4jRepository<Filme, String> {}