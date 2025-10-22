package learning.journalapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "journal_entry_id", nullable = false)
  private JournalEntry journalEntry;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String dosage;

  @Column(nullable = false)
  private LocalDateTime timeTaken;
}
