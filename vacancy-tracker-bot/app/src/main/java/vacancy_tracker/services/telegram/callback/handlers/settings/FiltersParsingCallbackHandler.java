package vacancy_tracker.services.telegram.callback.handlers.settings;

import lombok.AccessLevel;
import lombok.Getter;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.MessageCommand;
import vacancy_tracker.services.telegram.settings.SettingsService;

@Getter(AccessLevel.PROTECTED)
public abstract class FiltersParsingCallbackHandler<T> extends ParsingDataCallbackHandler<T> {

    private final SettingsService settingsService;

    protected FiltersParsingCallbackHandler(String callbackKey,
                                            SettingsService settingsService,
                                            MessageCommand command) {
        super(callbackKey, command);
        this.settingsService = settingsService;
    }

    protected abstract void changeSettings(T value, VacancySearchFilter filter);

    @Override
    public final void handleCastedData(T data, MessageData messageData) {
        var chatId = messageData.getChatId();
        var filters = settingsService.getFilters(chatId);

        changeSettings(data, filters);
        settingsService.saveFilters(chatId, filters);
        getHandler().endExecution(messageData);
    }
}