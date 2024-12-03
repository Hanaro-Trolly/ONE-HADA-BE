package com.example.onehada.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onehada.db.entity.Shortcut;

public interface ShortcutRepository extends JpaRepository<Shortcut, Long> {

	List<Shortcut> findByShortcutId(Long shortcutId);

	List<Shortcut> findShortByUserUserId(Long userId);
}
