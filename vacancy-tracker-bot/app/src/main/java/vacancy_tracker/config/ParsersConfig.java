package vacancy_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_REGION;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_TOWN;
import static vacancy_tracker.model.telegram.callback.VacanciesCallbackKeys.GET_VACANCIES;

@Configuration
public class ParsersConfig {

    @Bean
    public PaginationCallbackParser regionsPaginationCallbackParser() {
        return new PaginationCallbackParser(SET_REGION.getKey());
    }

    @Bean
    public PaginationCallbackParser townsPaginationCallbackParser() {
        return new PaginationCallbackParser(SET_TOWN.getKey());
    }

    @Bean
    public PaginationCallbackParser vacanciesPaginationCallbackParser() {
        return new PaginationCallbackParser(GET_VACANCIES.getKey());
    }
}
