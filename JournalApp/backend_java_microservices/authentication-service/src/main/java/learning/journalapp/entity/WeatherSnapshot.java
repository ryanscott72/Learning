package learning.journalapp.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSnapshot {

  @Enumerated(EnumType.STRING)
  private WeatherCondition condition;

  private Double temperatureCelsius;

  private Integer humidity; // TODO Hmm, do I want this

  // Helper method to get temperature in preferred unit
  public Double getTemperature(final TemperatureUnit unit) {
    if (temperatureCelsius == null) return null;

    return switch (unit) {
      case CELSIUS -> temperatureCelsius;
      case FAHRENHEIT -> (temperatureCelsius * 9.0 / 5.0) + 32.0;
    };
  }
}
