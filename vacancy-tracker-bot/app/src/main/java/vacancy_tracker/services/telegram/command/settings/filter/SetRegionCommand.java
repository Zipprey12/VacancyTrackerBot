package vacancy_tracker.services.telegram.command.settings.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.ExtendedRegion;
import vacancy_tracker.model.api.Location;
import vacancy_tracker.model.telegram.CallingSource;
import vacancy_tracker.model.telegram.dto.LocationSearch;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.api.location.LocationsService;
import vacancy_tracker.services.telegram.actions.message.AfterRegionSelectedMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.LocationInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.filter.RegionsSelectionMessageFormatter;

@Component
@Slf4j
public class SetRegionCommand extends InputInterceptingCommand<LocationSearch> {

    public static final String KEY = "/set_region";
    public static final String DESCRIPTION = "Установка региона поиска";

    private final RegionsSelectionMessageFormatter formatter;
    private final AfterRegionSelectedMessage regionSelectionUpdateMessage;
    private final LocationsService locationsService;
    private final SearchFiltersService settingsService;

    public SetRegionCommand(SendingAndUpdatingMessagePublisher publisher,
                            FiltersChangingCompletionHandler handler,
                            RegionsSelectionMessageFormatter messageFormatter,
                            SessionsService sessionsService,
                            AfterRegionSelectedMessage regionSelectionUpdateMessage,
                            LocationsService locationsService,
                            SearchFiltersService settingsService) {

        super(KEY, DESCRIPTION, publisher, handler, new LocationInterceptor(), sessionsService);
        this.formatter = messageFormatter;
        this.regionSelectionUpdateMessage = regionSelectionUpdateMessage;
        this.locationsService = locationsService;
        this.settingsService = settingsService;
        setTriggerEvent(false);
    }

    public void endExecution(MessageData messageData, ExtendedRegion region) {
        disableInterceptor(messageData.getChatId());
        regionSelectionUpdateMessage.publish(messageData, region);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        formatter.fillMessage(messageData);
    }

    @Override
    protected void executeWithParameters(MessageData messageData, LocationSearch parameter) {
        if (parameter.isText()) {
            showFiltered(messageData.getChatId(), parameter.getText());
        } else {
            handleRegionCode(parameter.getCode(), messageData);
        }
    }

    private void showFiltered(long chatId, String filter) {
        var outgoingMessage = new OutgoingMessage(MessageData.builder()
                .source(CallingSource.CHAT)
                .chatId(chatId)
                .build());
        formatter.fillMessage(outgoingMessage, filter);
        getPublisher().publish(outgoingMessage);
    }

    private void handleRegionCode(int regionCode, MessageData messageData) {
        var foundRegion = locationsService.getRegionByCode(regionCode);
        if (foundRegion.isEmpty()) {
            log.error("Произошла ошибка при выборе региона. Регион с Id '{}' не найден", regionCode);
            return;
        }
        var region = foundRegion.get();
        if (region.getTowns() == null || region.getTowns().isEmpty()) {
            endExecution(messageData);
        } else {
            endExecution(messageData, region);
        }
        selectRegion(region, messageData);
    }

    private void selectRegion(ExtendedRegion region, MessageData messageData) {
        var filters = settingsService.get(messageData.getChatId());
        var location = createLocation(region);
        filters.setLocation(location);
        settingsService.save(messageData.getChatId(), filters);
    }

    private Location createLocation(ExtendedRegion region) {
        var location = new Location();
        region.setTowns(null);
        location.setRegion(region);
        return location;
    }
}
