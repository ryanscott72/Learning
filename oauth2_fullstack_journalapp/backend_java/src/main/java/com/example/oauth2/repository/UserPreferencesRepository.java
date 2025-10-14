package com.example.oauth2.repository;

import com.example.oauth2.model.UserPreferences;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
  Optional<UserPreferences> findByUserId(final Long userId);
}
