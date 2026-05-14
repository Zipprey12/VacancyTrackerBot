package vacancy_tracker.services.telegram.callback.handlers.settings;

import lombok.AccessLevel;
import lombok.Getter;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.settings.SearchFiltersCommand;
import vacancy_tracker.services.telegram.settings.SettingsService;

@Getter(AccessLevel.PROTECTED)
public abstract class FiltersParsingCallbackHandler<T> extends ParsingDataCallbackHandler<T> {

    private final SettingsService settingsService;
    private final SearchFiltersCommand searchFiltersCommand;

    protected FiltersParsingCallbackHandler(String callbackKey,
                                            SettingsService settingsService,
                                            SearchFiltersCommand searchFiltersCommand) {
        super(callbackKey, searchFiltersCommand);
        this.settingsService = settingsService;
        this.searchFiltersCommand = searchFiltersCommand;
    }

    protected abstract void changeSettings(T value, VacancySearchFilter filter);

    @Override
    public final void handleCastedData(T data, MessageData messageData) {
        var chatId = messageData.getChatId();
        var filters = settingsService.getFilters(chatId);
        changeSettings(data, filters);

        settingsService.saveFilters(chatId, filters);
        searchFiltersCommand.handleExecutionEnd(messageData, false);
    }
}
