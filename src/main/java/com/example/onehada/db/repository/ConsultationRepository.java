package com.example.onehada.db.repository;

import com.example.onehada.db.entity.Consultation;
import com.example.onehada.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
	List<Consultation> findByUser(User user);

	List<Consultation> findByUserUserId(Long userId);
}
