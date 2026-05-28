package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.Location;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.LocationSearch;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.api.location.LocationsService;
import vacancy_tracker.services.telegram.callback.handlers.NavigationCallbackHandler;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.settings.filter.SetTownCommand;
import vacancy_tracker.services.telegram.view.formatters.filter.TownsSelectionMessageFormatter;

import java.util.Optional;

@Slf4j
@Component
public class SetTownCallbackHandler extends NavigationCallbackHandler<LocationSearch> {

    public static final String KEY = FilterSettingsCallbackKeys.SET_TOWN.getKey();

    private final LocationsService locationsService;
    private final MessagePublisher publisher;
    private final TownsSelectionMessageFormatter messageFormatter;

    public SetTownCallbackHandler(SetTownCommand setTownCommand,
                                  SendingAndUpdatingMessagePublisher publisher,
                                  LocationsService locationsService,
                                  TownsSelectionMessageFormatter messageFormatter) {

        super(KEY, setTownCommand);

        this.locationsService = locationsService;
        this.publisher = publisher;
        this.messageFormatter = messageFormatter;
    }

    @Override
    protected Optional<LocationSearch> tryCastSelectedValue(String value) {
        return LocationSearch.parse(value);
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

        var regionArg = args.atIndex(0).getValue();
        var regionId = StringUtil.parseInt(regionArg);
        if (regionId.isEmpty()) {
            handleUnexpectedValue(outgoingMessage);
            log.error("Передан недопустимый id региона");
            return;
        }

        var location = locationsService.getLocationByRegionCode(regionId.get());
        if (location.isEmpty()) {
            handleUnexpectedValue(outgoingMessage);
            log.error("Передан недопустимый id региона");
            return;
        }

        messageFormatter.fillMessage(outgoingMessage, location.get(),
                data.targetPage(), args.size() >= 2 ? args.atIndex(1).getValue() : null);
        publisher.publish(outgoingMessage);
    }

    private void handleUnexpectedValue(OutgoingMessage outgoingMessage) {
        messageFormatter.fillMessage(outgoingMessage, new Location());
        publisher.publish(outgoingMessage);
    }
}