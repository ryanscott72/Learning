package learning.journalapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "journal_entries",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "entry_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "entry_date", nullable = false)
  private LocalDate entryDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Mood mood;

  @Embedded private WeatherSnapshot weather;

  @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Medication> medications = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  private Season season;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();

    // Auto-calculate season if not set
    if (season == null && entryDate != null) {
      season = calculateSeason(entryDate);
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  private Season calculateSeason(final LocalDate date) {
    final int month = date.getMonthValue();
    if (month >= 3 && month <= 5) return Season.SPRING;
    if (month >= 6 && month <= 8) return Season.SUMMER;
    if (month >= 9 && month <= 11) return Season.FALL;
    return Season.WINTER;
  }

  // Helper method to add medication
  public void addMedication(final Medication medication) {
    medications.add(medication);
    medication.setJournalEntry(this);
  }

  // Helper method to remove medication
  public void removeMedication(final Medication medication) {
    medications.remove(medication);
    medication.setJournalEntry(null);
  }
}
