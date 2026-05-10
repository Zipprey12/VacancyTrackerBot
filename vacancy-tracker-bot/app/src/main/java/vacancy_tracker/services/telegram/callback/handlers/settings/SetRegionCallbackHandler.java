package vacancy_tracker.services.telegram.callback.handlers.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.entity.Location;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.view.PaginationCallbackData;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.NavigationCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.SetRegionCommand;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.operations.AfterRegionSelectedMessage;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.PaginatedKeyboardBuilder;
import vacancy_tracker.services.vacancy.LocationsService;

@Slf4j
@Component
public class SetRegionCallbackHandler extends NavigationCallbackHandler {

    public static final String KEY = FilterSettingsCallbackKeys.SELECT_REGION.getKey();

    private final SettingsService settingsService;
    private final LocationsService locationsService;
    private final SetRegionCommand setRegionCommand;
    private final AfterRegionSelectedMessage regionSelectionUpdateMessage;

    public SetRegionCallbackHandler(SetRegionCommand setLocationCommand,
                                    @Qualifier("regionsPaginationBuilder") PaginatedKeyboardBuilder keyboardBuilder,
                                    MessageEditor messageEditor,
                                    SettingsService settingsService,
                                    LocationsService locationsService,
                                    AfterRegionSelectedMessage regionSelectionUpdateMessage) {

        super(KEY, keyboardBuilder, messageEditor);

        this.settingsService = settingsService;
        this.locationsService = locationsService;
        this.setRegionCommand = setLocationCommand;
        this.regionSelectionUpdateMessage = regionSelectionUpdateMessage;
    }

    @Override
    protected void select(PaginationCallbackData data, MessageData message) {
        var selected = data.getSelectedKey();
        var id = StringUtil.parseInt(selected);

        if (id.isEmpty()) {
            return;
        }
        var regionId = id.get();
        var foundRegion = locationsService.getRegionById(regionId);
        if (foundRegion.isEmpty()) {
            log.error("Произошла ошибка при выборе региона. Регион с Id '{}' не найден", regionId);
            return;
        }

        var region = foundRegion.get();
        if(region.getTowns() == null || region.getTowns().isEmpty()){
            setRegionCommand.handleExecutionEnd(message, false);
        }
        else {
            foundRegion.ifPresent(value -> regionSelectionUpdateMessage.update(message, value));
        }
        selectRegion(region, message.getChatId());
    }

    @Override
    protected void executeWithNoArgs(MessageData messageData) {
        setRegionCommand.execute(messageData, true);
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
