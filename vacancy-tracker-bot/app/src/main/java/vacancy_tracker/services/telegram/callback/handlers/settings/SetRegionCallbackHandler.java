package vacancy_tracker.services.telegram.callback.handlers.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.entity.Location;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.NavigationCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.SetRegionCommand;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.operations.AfterRegionSelectedMessage;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.PaginatedKeyboardBuilder;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.Optional;

@Slf4j
@Component
public class SetRegionCallbackHandler extends NavigationCallbackHandler<Integer> {

    public static final String KEY = FilterSettingsCallbackKeys.SET_REGION.getKey();

    private final SettingsService settingsService;
    private final LocationsService locationsService;
    private final SetRegionCommand setRegionCommand;
    private final AfterRegionSelectedMessage regionSelectionUpdateMessage;

    public SetRegionCallbackHandler(SetRegionCommand setRegionCommand,
                                    MessageEditor messageEditor,
                                    SettingsService settingsService,
                                    LocationsService locationsService,
                                    PaginatedKeyboardBuilder regionsPaginationBuilder,
                                    AfterRegionSelectedMessage regionSelectionUpdateMessage) {

        super(KEY, regionsPaginationBuilder, setRegionCommand, messageEditor);

        this.settingsService = settingsService;
        this.locationsService = locationsService;
        this.setRegionCommand = setRegionCommand;
        this.regionSelectionUpdateMessage = regionSelectionUpdateMessage;
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
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
            setRegionCommand.handleExecutionEnd(messageData, false);
        } else {
            foundRegion.ifPresent(value -> regionSelectionUpdateMessage.update(messageData, value));
        }
        selectRegion(region, messageData.getChatId());
    }

    private void selectRegion(Region region, long sessionId) {
        var filters = settingsService.getFilters(sessionId);
        var location = createLocation(region);
        filters.setLocation(location);
        settingsService.saveFilters(sessionId, filters);
    }

    private Location createLocation(Region region) {
        var location = new Location();
        region.setTowns(null);
        location.setRegion(region);
        return location;
    }
}
