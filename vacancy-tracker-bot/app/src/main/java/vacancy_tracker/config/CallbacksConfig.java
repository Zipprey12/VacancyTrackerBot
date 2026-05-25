package vacancy_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;
import vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder;

@Configuration
@DependsOn("initializer")
public class CallbacksConfig {

    @Bean
    public PaginatedKeyboardBuilder regionsPaginationBuilder(PaginationCallbackParser regionsPaginationCallbackParser) {
        return new PaginatedKeyboardBuilder(regionsPaginationCallbackParser);
    }

    @Bean
    public PaginatedKeyboardBuilder townsPaginationBuilder(PaginationCallbackParser townsPaginationCallbackParser) {
        return new PaginatedKeyboardBuilder(townsPaginationCallbackParser);
    }
}