package zerobase.weatherprojectstudy.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "date_weather")
@Builder
public class DateWeather {
    @Id
    private LocalDate date;

    private String weather;
    private String icon;
    private double temperature;
}
