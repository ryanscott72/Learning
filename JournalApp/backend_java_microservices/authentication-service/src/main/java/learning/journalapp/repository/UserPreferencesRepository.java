package learning.journalapp.repository;

import java.util.Optional;
import learning.journalapp.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
  Optional<UserPreferences> findByUserId(final Long userId);
}
