package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.entity.Location;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.NavigationCallbackHandler;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.command.publishers.UpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.settings.search.SetRegionCommand;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.filter.RegionsSelectionMessageFormatter;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.Optional;

@Slf4j
@Component
public class SetRegionCallbackHandler extends NavigationCallbackHandler<Integer> {

    public static final String KEY = FilterSettingsCallbackKeys.SET_REGION.getKey();

    private final SearchFiltersService settingsService;
    private final LocationsService locationsService;
    private final SetRegionCommand setRegionCommand;
    private final RegionsSelectionMessageFormatter messageFormatter;
    private final MessagePublisher publisher;

    public SetRegionCallbackHandler(SetRegionCommand setRegionCommand,
                                    RegionsSelectionMessageFormatter messageFormatter,
                                    UpdatingMessagePublisher messagePublisher,
                                    SearchFiltersService settingsService,
                                    LocationsService locationsService) {

        super(KEY, setRegionCommand);

        this.settingsService = settingsService;
        this.locationsService = locationsService;
        this.setRegionCommand = setRegionCommand;
        this.messageFormatter = messageFormatter;
        this.publisher = messagePublisher;
    }

    @Override
    public void handleCastedData(Integer regionId, MessageData messageData) {
        var foundRegion = locationsService.getRegionById(regionId);
        if (foundRegion.isEmpty()) {
            log.error("Произошла ошибка при выборе региона. Регион с Id '{}' не найден", regionId);
            return;
        }

        var region = foundRegion.get();
        if (region.getTowns() == null || region.getTowns().isEmpty()) {
            setRegionCommand.endExecution(messageData);
        } else {
            foundRegion.ifPresent(value -> setRegionCommand.endExecution(messageData, region));
        }
        selectRegion(region, messageData);
    }

    @Override
    protected void navigate(MessageData message, CallbackData data) {
        var page = data.targetPage();
        var args = data.args();
        var filter = args == null ? null : args.getFirst();
        var outgoingMessage = new OutgoingMessage(message);
        messageFormatter.fillMessage(outgoingMessage, filter, page);
        publisher.publish(outgoingMessage);
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
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
