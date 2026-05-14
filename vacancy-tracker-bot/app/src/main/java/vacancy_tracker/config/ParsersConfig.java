package vacancy_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;

@Configuration
public class ParsersConfig {

    @Bean
    public PaginationCallbackParser regionsPaginationCallbackParser() {
        return new PaginationCallbackParser(FilterSettingsCallbackKeys.SET_REGION.getKey());
    }

    @Bean
    public PaginationCallbackParser townsPaginationCallbackParser(){
        return new PaginationCallbackParser(FilterSettingsCallbackKeys.SET_TOWN.getKey());
    }
}
