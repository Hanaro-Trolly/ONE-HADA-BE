package com.example.onehada.customer.agent;

import com.example.onehada.customer.agent.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Transactional
public interface AgentRepository extends JpaRepository<Agent, Long> {
	Optional<Agent> findByAgentEmailAndAgentPw(String email, String password);
	List<Agent> findByAgentEmailContaining(String email);
	List<Agent> findByAgentNameContaining(String name);
}
