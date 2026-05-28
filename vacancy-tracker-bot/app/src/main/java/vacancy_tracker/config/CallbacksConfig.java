package vacancy_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;
import vacancy_tracker.services.telegram.view.keyboard.CallbackPaginatedKeyboardBuilder;
import vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder;

@Configuration
@DependsOn("initializer")
public class CallbacksConfig {

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