package com.example.onehada.customer.shortcut;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onehada.customer.shortcut.Shortcut;

public interface ShortcutRepository extends JpaRepository<Shortcut, Long> {
	List<Shortcut> findShortcutByUserUserId(Long userId);

	Shortcut findByShortcutId(Long shortcutId);

	// @Query("SELECT s FROM Shortcut s WHERE s.user.userId = :userId AND s.isFavorite = true")
	List<Shortcut> findShortcutByUserUserIdAndIsFavoriteTrue(Long userId);

}