package vacancy_tracker.services.telegram.callback.handlers.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.entity.Town;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.NavigationCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.SetTownCommand;
import vacancy_tracker.services.telegram.mappers.CallbackItemMapper;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.Optional;

@Slf4j
@Component
public class SetTownCallbackHandler extends NavigationCallbackHandler<Integer> {

    public static final String KEY = FilterSettingsCallbackKeys.SET_TOWN.getKey();

    private final LocationsService locationsService;
    private final SettingsService settingsService;
    private final SetTownCommand setTownCommand;
    private final CallbackItemMapper mapper;

    public SetTownCallbackHandler(SetTownCommand setTownCommand,
                                  MessageEditor messageEditor,
                                  SettingsService settingsService,
                                  LocationsService locationsService,
                                  CallbackItemMapper mapper) {

        super(KEY, setTownCommand, messageEditor);

        this.locationsService = locationsService;
        this.settingsService = settingsService;
        this.setTownCommand = setTownCommand;
        this.mapper = mapper;
    }

    @Override
    protected Optional<Integer> tryCastSelectedValue(String value) {
        return StringUtil.parseInt(value);
    }

    @Override
    public void handleCastedData(Integer id, MessageData messageData) {
        var town = selectTown(id, messageData.getChatId());
        if (town.isPresent()) {
            setTownCommand.endExecution(messageData);
        }
    }

    @Override
    protected void navigate(MessageData message, CallbackData data) {
        var page = data.targetPage();
        var args = data.args();
        if (args == null || args.isEmpty()) {
            log.error("Не удалось вывести города, т.к. в Callback не было id региона");
            return;
        }
        var regionId = StringUtil.parseInt(args);
        if (regionId.isEmpty()) {
            log.error("Передан недопустимый id региона");
            return;
        }

        var region = locationsService.getRegionById(regionId.get());
        if (region.isEmpty()) {
            log.error("Передан недопустимый id региона");
            return;
        }

        var items = mapper.fromTowns(region.get().getTowns());
        var keyboard = getKeyboardBuilder().build(items, page, data.args());
        editKeyboard(message.getChatId(), message.getMessageId(), keyboard);
    }

    private Optional<Town> selectTown(int townId, long sessionId) {
        var filters = settingsService.getFilters(sessionId);
        var location = locationsService.getLocationByTownId(townId);
        if (location.isPresent()) {
            filters.setLocation(location.get());
            settingsService.saveFilters(sessionId, filters);
            return Optional.ofNullable(location.get().getTown());
        }
        log.error("Произошла ошибка при выборе региона. Регион с Id '{}' не найден", townId);
        return Optional.empty();
    }
}
