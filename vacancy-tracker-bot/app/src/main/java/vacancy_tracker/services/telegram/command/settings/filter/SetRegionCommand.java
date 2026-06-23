package vacancy_tracker.services.telegram.command.settings.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.domain.Location;
import vacancy_tracker.model.domain.Region;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.command.CommandCategory;
import vacancy_tracker.model.telegram.dto.LocationSearch;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.session.PublishType;
import vacancy_tracker.services.api.location.LocationsService;
import vacancy_tracker.services.telegram.actions.message.AfterRegionSelectedMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.LocationInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.filter.RegionsSelectionMessageFormatter;

@Component
@Slf4j
public class SetRegionCommand extends InputInterceptingCommand<LocationSearch> {

    public static final String KEY = "/region";
    public static final String DESCRIPTION = "Регион";

    private final RegionsSelectionMessageFormatter formatter;
    private final AfterRegionSelectedMessage regionSelectionUpdateMessage;
    private final LocationsService locationsService;
    private final SearchFiltersService settingsService;
    private final FiltersChangingCompletionHandler completionHandler;

    public SetRegionCommand(SendingAndUpdatingMessagePublisher publisher,
                            FiltersChangingCompletionHandler handler,
                            RegionsSelectionMessageFormatter messageFormatter,
                            SessionsService sessionsService,
                            AfterRegionSelectedMessage regionSelectionUpdateMessage,
                            LocationsService locationsService,
                            SearchFiltersService settingsService,
                            SequentialAsyncExecutionStrategy strategy) {

        super(new CommandArgs(KEY, DESCRIPTION, handler, CommandCategory.FILTER), publisher, new LocationInterceptor(), sessionsService, strategy);
        this.formatter = messageFormatter;
        this.regionSelectionUpdateMessage = regionSelectionUpdateMessage;
        this.locationsService = locationsService;
        this.settingsService = settingsService;
        this.completionHandler = handler;
        setTriggerEvent(false);
    }

    public void endExecution(MessageData messageData, Region region) {
        disableInterceptor(messageData.getChatId());
        completionHandler.updateNotificationSettings(messageData.getChatId());
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
        } else if (parameter.isCode()) {
            handleRegionCode(parameter.getCode(), messageData);
        } else {
            handleEmpty(messageData.getChatId());
        }
    }

    private void showFiltered(long chatId, String filter) {
        var outgoingMessage = new OutgoingMessage(MessageData.builder()
                .source(PublishType.SEND)
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

    private void handleEmpty(long chatId) {
        var settings = settingsService.get(chatId);
        settings.setLocation(null);
        settingsService.save(chatId, null);
    }

    private void selectRegion(Region region, MessageData messageData) {
        var filters = settingsService.get(messageData.getChatId());
        var location = createLocation(region);
        filters.setLocation(location);
        settingsService.save(messageData.getChatId(), filters);
    }

    private Location createLocation(Region region) {
        var location = new Location();
        region.setTowns(null);
        location.setRegion(region);
        return location;
    }
}
