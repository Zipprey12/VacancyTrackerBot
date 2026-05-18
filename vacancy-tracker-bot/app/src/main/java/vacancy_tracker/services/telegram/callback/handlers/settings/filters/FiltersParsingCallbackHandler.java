package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import lombok.AccessLevel;
import lombok.Getter;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.callback.handlers.ParsingDataCallbackHandler;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;

@Getter(AccessLevel.PROTECTED)
public abstract class FiltersParsingCallbackHandler<T> extends ParsingDataCallbackHandler<T> {

    private final SearchFiltersService settingsService;

    protected FiltersParsingCallbackHandler(String callbackKey,
                                            SearchFiltersService settingsService,
                                            CompletableMessageCommand command) {
        super(callbackKey, command);
        this.settingsService = settingsService;
    }

    protected abstract void changeSettings(T data, VacancySearchFilter filter);

    @Override
    public final void handleCastedData(T data, MessageData messageData) {
        var chatId = messageData.getChatId();
        var filters = settingsService.get(chatId);

        changeSettings(data, filters);
        settingsService.save(chatId, filters);
        getHandler().endExecution(messageData);
    }
}