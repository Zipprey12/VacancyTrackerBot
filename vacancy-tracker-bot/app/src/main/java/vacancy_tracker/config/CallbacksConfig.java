package vacancy_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;
import vacancy_tracker.services.telegram.view.keyboard.CallbackPaginatedKeyboardBuilder;
import vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_REGION;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_TOWN;
import static vacancy_tracker.model.telegram.callback.VacanciesCallbackKeys.GET_VACANCIES;

@Configuration
@DependsOn("initializer")
public class CallbacksConfig {

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

    @Bean
    public CallbackPaginatedKeyboardBuilder regionsPaginationBuilder(PaginationCallbackParser regionsPaginationCallbackParser) {
        return new CallbackPaginatedKeyboardBuilder(regionsPaginationCallbackParser);
    }

    @Bean
    public CallbackPaginatedKeyboardBuilder townsPaginationBuilder(PaginationCallbackParser townsPaginationCallbackParser) {
        return new CallbackPaginatedKeyboardBuilder(townsPaginationCallbackParser);
    }

    @Bean
    public PaginatedKeyboardBuilder vacanciesPaginationBuilder(PaginationCallbackParser vacanciesPaginationCallbackParser) {
        return new PaginatedKeyboardBuilder(vacanciesPaginationCallbackParser);
    }
}