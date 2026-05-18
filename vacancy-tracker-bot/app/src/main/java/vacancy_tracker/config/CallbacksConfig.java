package vacancy_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import vacancy_tracker.services.telegram.callback.handlers.CallbackHandler;
import vacancy_tracker.services.telegram.callback.handlers.settings.filters.*;
import vacancy_tracker.services.telegram.callback.handlers.settings.notification.ToggleEmptyNotificationHandler;
import vacancy_tracker.services.telegram.callback.handlers.settings.notification.ToggleNotificationCallbackHandler;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;
import vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder;

import java.util.List;

@Configuration
@DependsOn("initializer")
public class CallbacksConfig {

    @Bean
    public List<CallbackHandler> callbackHandlers(SetMaxSalaryCallbackHandler setMaxSalaryCallbackHandler,
                                                  SetMinSalaryCallbackHandler setMinSalaryCallbackHandler,
                                                  SetSearchingTextCallbackHandler setSearchingTextCallbackHandler,
                                                  SetLocationCallbackHandler setLocationCallbackHandler,
                                                  SetRegionCallbackHandler setRegionCallbackHandler,
                                                  SetExperienceCallbackHandler setExperienceCallbackHandler,
                                                  SetTownCallbackHandler setTownCallbackHandler,
                                                  CancelChangeCallbackHandler cancelChangeCallbackHandler,
                                                  ResetFiltersCallbackHandler resetFiltersCallbackHandler,
                                                  ToggleNotificationCallbackHandler onOffNotificationCallbackHandler,
                                                  ToggleEmptyNotificationHandler toggleEmptyNotificationHandler) {
        return List.of(
                setMaxSalaryCallbackHandler,
                setMinSalaryCallbackHandler,
                setSearchingTextCallbackHandler,
                setLocationCallbackHandler,
                setRegionCallbackHandler,
                setExperienceCallbackHandler,
                setTownCallbackHandler,
                cancelChangeCallbackHandler,
                resetFiltersCallbackHandler,
                onOffNotificationCallbackHandler,
                toggleEmptyNotificationHandler
        );
    }

    @Bean
    public PaginatedKeyboardBuilder regionsPaginationBuilder(PaginationCallbackParser regionsPaginationCallbackParser) {
        return new PaginatedKeyboardBuilder(regionsPaginationCallbackParser);
    }

    @Bean
    public PaginatedKeyboardBuilder townsPaginationBuilder(PaginationCallbackParser townsPaginationCallbackParser) {
        return new PaginatedKeyboardBuilder(townsPaginationCallbackParser);
    }
}