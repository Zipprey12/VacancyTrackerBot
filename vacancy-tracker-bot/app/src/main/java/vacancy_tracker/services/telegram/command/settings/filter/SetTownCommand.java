package vacancy_tracker.services.telegram.command.settings.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.domain.Town;
import vacancy_tracker.model.telegram.dto.LocationSearch;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.session.CallingSource;
import vacancy_tracker.services.api.location.LocationsService;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.LocationInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.filter.TownsSelectionMessageFormatter;

import java.util.Optional;

@Component
@Slf4j
public class SetTownCommand extends InputInterceptingCommand<LocationSearch> {

    public static final String KEY = "/set_town";
    public static final String DESCRIPTION = "Установка города поиска";

    private final SearchFiltersService settingsService;
    private final TownsSelectionMessageFormatter formatter;
    private final LocationsService locationsService;

    public SetTownCommand(SendingAndUpdatingMessagePublisher publisher,
                          FiltersChangingCompletionHandler handler,
                          SessionsService sessionsService,
                          SearchFiltersService settingsService,
                          TownsSelectionMessageFormatter formatter,
                          LocationsService locationsService,
                          SequentialAsyncExecutionStrategy strategy) {
        super(KEY, DESCRIPTION, publisher, handler,
                new LocationInterceptor(), sessionsService, strategy);

        this.settingsService = settingsService;
        this.formatter = formatter;
        this.locationsService = locationsService;
        setTriggerEvent(false);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = settingsService.get(messageData.getChatId());
        var location = settings.getLocation();
        formatter.fillMessage(messageData, location);
    }

    @Override
    protected void executeWithParameters(MessageData messageData, LocationSearch parameter) {
        if (parameter.isText()) {
            showFiltered(messageData.getChatId(), parameter.getText());
        } else {
            handleCode(parameter.getCode(), messageData);
        }
    }

    private void showFiltered(long chatId, String filter) {
        var filters = settingsService.get(chatId);
        var location = filters.getLocation();
        var outgoingMessage = new OutgoingMessage(MessageData.builder()
                .source(CallingSource.CHAT)
                .chatId(chatId)
                .build());

        formatter.fillMessage(outgoingMessage, location, 0, filter);
        getPublisher().publish(outgoingMessage);
    }

    private void handleCode(int townId, MessageData messageData) {
        var town = selectTown(townId, messageData.getChatId());
        if (town.isPresent()) {
            endExecution(messageData, true);
        }
    }

    private Optional<Town> selectTown(int townId, long sessionId) {
        var filters = settingsService.get(sessionId);
        var location = locationsService.getLocationByTownId(townId);
        if (location.isPresent()) {
            filters.setLocation(location.get());
            settingsService.save(sessionId, filters);
            return Optional.ofNullable(location.get().getTown());
        }
        log.error("Произошла ошибка при выборе региона. Регион с Id '{}' не найден", townId);
        return Optional.empty();
    }
}