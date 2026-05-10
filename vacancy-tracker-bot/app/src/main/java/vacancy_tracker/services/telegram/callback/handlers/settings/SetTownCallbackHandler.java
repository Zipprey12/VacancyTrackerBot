package vacancy_tracker.services.telegram.callback.handlers.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.entity.Town;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.PaginationCallbackData;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.NavigationCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.SetTownCommand;
import vacancy_tracker.services.telegram.mappers.CallbackItemMapper;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.PaginatedKeyboardBuilder;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.Optional;

@Slf4j
@Component
public class SetTownCallbackHandler extends NavigationCallbackHandler {

    public static final String KEY = FilterSettingsCallbackKeys.SELECT_TOWN.getKey();

    private final LocationsService locationsService;
    private final SettingsService settingsService;
    private final SetTownCommand setTownCommand;
    private final CallbackItemMapper mapper;

    public SetTownCallbackHandler(SetTownCommand setTownCommand,
                                  @Qualifier("townsPaginationBuilder") PaginatedKeyboardBuilder keyboardBuilder,
                                  MessageEditor messageEditor,
                                  SettingsService settingsService,
                                  LocationsService locationsService,
                                  CallbackItemMapper mapper) {

        super(KEY, keyboardBuilder, messageEditor);

        this.locationsService = locationsService;
        this.settingsService = settingsService;
        this.setTownCommand = setTownCommand;
        this.mapper = mapper;
    }

    @Override
    protected void select(PaginationCallbackData data, MessageData messageData) {
        var selected = data.getSelectedKey();
        var id = StringUtil.parseInt(selected);

        if (id.isEmpty()) {
            setTownCommand.execute(messageData, true);
            return;
        }

        var town = selectTown(id.get(), messageData.getChatId());
        if (town.isPresent()) {
            setTownCommand.handleExecutionEnd(messageData, false);
        }
    }

    @Override
    protected void executeWithNoArgs(MessageData messageData) {
        setTownCommand.execute(messageData, true);
    }

    @Override
    protected void navigate(MessageData message, PaginationCallbackData data) {
        var page = data.getTargetPage();
        var args = data.getArgs();
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
        //todo здесь будет работать некорректно при асинхронных вызовах
        //надо продумать класс так, чтоб можно было передать туда список CallbackItem
        getKeyboardBuilder().setItems(items);
        var keyboard = getKeyboardBuilder().build(page, data.getArgs());
        editKeyboard(message.getChatId(), message.getMessageId(), keyboard);
    }

    private Optional<Town> selectTown(int townId, long sessionId) {
        var filters = settingsService.getFilters(sessionId);
        var location = locationsService.getLocationByTownId(townId);
        if (location.isPresent()) {
            filters.setLocation(location.get());
            return Optional.ofNullable(location.get().getTown());
        }
        log.error("Произошла ошибка при выборе региона. Регион с Id '{}' не найден", townId);
        return Optional.empty();
    }
}
