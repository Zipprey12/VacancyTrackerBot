package vacancy_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RegionsConfig {

    @Bean
    public Map<Integer, String> additionalRegionNames() {
        return Map.of(
                71, "Тюменская область, включая Ханты-Мансийский АО и Ямало-Ненецкий АО"
        );
    }
}
