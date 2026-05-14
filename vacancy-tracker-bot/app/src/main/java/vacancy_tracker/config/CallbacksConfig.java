package vacancy_tracker.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;
import vacancy_tracker.services.telegram.callback.handlers.CallbackHandler;
import vacancy_tracker.services.telegram.callback.handlers.settings.*;
import vacancy_tracker.services.telegram.mappers.CallbackItemMapper;
import vacancy_tracker.services.telegram.view.PaginatedKeyboardBuilder;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.LinkedList;
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
                                                  CancelChangeCallbackHandler cancelChangeCallbackHandler) {
        return List.of(
                setMaxSalaryCallbackHandler,
                setMinSalaryCallbackHandler,
                setSearchingTextCallbackHandler,
                setLocationCallbackHandler,
                setRegionCallbackHandler,
                setExperienceCallbackHandler,
                setTownCallbackHandler,
                cancelChangeCallbackHandler
        );
    }

    @Bean
    public PaginatedKeyboardBuilder regionsPaginationBuilder(@Qualifier("regionsPaginationCallbackParser")
                                                             PaginationCallbackParser parser,
                                                             LocationsService locationsService,
                                                             CallbackItemMapper mapper) {
        var builder = new PaginatedKeyboardBuilder(parser);
        var regions = locationsService.getAllRegionsBasic();
        var items = new LinkedList<CallbackItem>();

        regions.forEach(r -> items.add(mapper.fromRegion(r)));
        builder.setItems(items);
        return builder;
    }

    @Bean
    public PaginatedKeyboardBuilder townsPaginationBuilder(PaginationCallbackParser townsPaginationCallbackParser) {
        return new PaginatedKeyboardBuilder(townsPaginationCallbackParser);
    }
}