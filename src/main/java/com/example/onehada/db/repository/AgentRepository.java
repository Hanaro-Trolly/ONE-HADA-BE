package com.example.onehada.db.repository;

import com.example.onehada.db.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Integer> {
	Optional<Agent> findByAgentEmailAndAgentPw(String email, String password);
	List<Agent> findByAgentEmailContaining(String email);
	List<Agent> findByAgentNameContaining(String name);
}
