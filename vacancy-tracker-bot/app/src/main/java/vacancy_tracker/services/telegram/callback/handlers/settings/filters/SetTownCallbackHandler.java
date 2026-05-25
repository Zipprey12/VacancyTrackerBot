package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.entity.Location;
import vacancy_tracker.model.api.entity.Town;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.NavigationCallbackHandler;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.command.publishers.UpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.settings.search.SetTownCommand;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.filter.TownsSelectionMessageFormatter;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.Optional;

@Slf4j
@Component
public class SetTownCallbackHandler extends NavigationCallbackHandler<Integer> {

    public static final String KEY = FilterSettingsCallbackKeys.SET_TOWN.getKey();

    private final LocationsService locationsService;
    private final SearchFiltersService settingsService;
    private final SetTownCommand setTownCommand;
    private final MessagePublisher publisher;
    private final TownsSelectionMessageFormatter messageFormatter;

    public SetTownCallbackHandler(SetTownCommand setTownCommand,
                                  UpdatingMessagePublisher publisher,
                                  SearchFiltersService settingsService,
                                  LocationsService locationsService,
                                  TownsSelectionMessageFormatter messageFormatter) {

        super(KEY, setTownCommand);

        this.locationsService = locationsService;
        this.settingsService = settingsService;
        this.setTownCommand = setTownCommand;
        this.publisher = publisher;
        this.messageFormatter = messageFormatter;
    }

    @Override
    public void handleCastedData(Integer id, MessageData messageData) {
        var town = selectTown(id, messageData.getChatId());
        if (town.isPresent()) {
            setTownCommand.endExecution(messageData);
        }
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
    }

    @Override
    protected void navigate(MessageData message, CallbackData data) {
        var args = data.args();
        var outgoingMessage = new OutgoingMessage(message);

        if (args == null || args.isEmpty()) {
            handleUnexpectedValue(outgoingMessage);
            log.error("Не удалось вывести города, т.к. в Callback не было id региона");
            return;
        }

        var regionArg = args.getFirst();
        var regionId = StringUtil.parseInt(regionArg);
        if (regionId.isEmpty()) {
            handleUnexpectedValue(outgoingMessage);
            log.error("Передан недопустимый id региона");
            return;
        }

        var location = locationsService.getLocationByRegionId(regionId.get());
        if (location.isEmpty()) {
            handleUnexpectedValue(outgoingMessage);
            log.error("Передан недопустимый id региона");
            return;
        }
        messageFormatter.fillMessage(outgoingMessage, location.get(),
                data.targetPage(), args.size() >= 2 ? args.get(1) : null);
        publisher.publish(outgoingMessage);
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

    private void handleUnexpectedValue(OutgoingMessage outgoingMessage) {
        messageFormatter.fillMessage(outgoingMessage, new Location());
        publisher.publish(outgoingMessage);
    }
}