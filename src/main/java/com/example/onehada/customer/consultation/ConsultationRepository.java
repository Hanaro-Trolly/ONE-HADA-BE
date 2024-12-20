package com.example.onehada.customer.consultation;

import com.example.onehada.customer.agent.Agent;
import com.example.onehada.customer.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
	List<Consultation> findByUser(User user);

	List<Consultation> findByAgent(Agent agent);

	List<Consultation> findByUserUserId(Long userId);

	List<Consultation> findByUserUserIdOrderByConsultationIdDesc(Long userId);

	void deleteAllByUser(User user);
}
