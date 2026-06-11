package com.example.hub_entretenimento.repository;

import com.example.hub_entretenimento.domain.Genero;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneroRepository extends Neo4jRepository<Genero, String> {}