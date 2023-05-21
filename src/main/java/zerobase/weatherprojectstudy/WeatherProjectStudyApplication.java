package zerobase.weatherprojectstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WeatherProjectStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherProjectStudyApplication.class, args);
    }

}
