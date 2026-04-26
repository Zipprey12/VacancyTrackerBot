package vacancy_tracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vacancy_tracker.services.telegram.callback.handlers.CallbackHandler;
import vacancy_tracker.services.telegram.callback.handlers.SetMaxSalaryCallbackHandler;
import vacancy_tracker.services.telegram.callback.handlers.SetSearchingTextCallbackHandler;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CallbacksConfig {

    private final SetMaxSalaryCallbackHandler setMaxSalaryCallbackHandler;
    private final SetSearchingTextCallbackHandler setSearchingTextCallbackHandler;


    @Bean
    public List<CallbackHandler> callbackHandlers() {
        return List.of(
                setMaxSalaryCallbackHandler,
                setSearchingTextCallbackHandler
        );
    }
}