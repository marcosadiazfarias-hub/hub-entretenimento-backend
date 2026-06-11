package com.example.hub_entretenimento.repository;

import com.example.hub_entretenimento.domain.Diretor;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiretorRepository extends Neo4jRepository<Diretor, String> {}