package vacancy_tracker.services.telegram.command.settings.filter;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.command.CommandCategory;
import vacancy_tracker.model.telegram.dto.LocationSearch;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.settings.ResetFilterFieldType;
import vacancy_tracker.services.telegram.command.ExtendedMessageCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.RESET;
import static vacancy_tracker.model.telegram.settings.ResetFilterFieldType.ALL;
import static vacancy_tracker.model.telegram.settings.ResetFilterFieldType.LOCATION;

@Component
public class ResetFiltersCommand extends ExtendedMessageCommand<ResetFilterFieldType> {

    public static final String KEY = "/reset_filters";
    public static final String DESCRIPTION = "Сбросить фильтры";

    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    private final SessionsService sessionsService;
    private final SearchFiltersService settingsService;
    private final SetRegionCommand setRegionCommand;

    public ResetFiltersCommand(SendingAndUpdatingMessagePublisher publisher,
                               FiltersChangingCompletionHandler completionHandler,
                               SessionsService sessionsService,
                               SearchFiltersService settingsService,
                               SequentialAsyncExecutionStrategy strategy,
                               SetRegionCommand setRegionCommand) {
        super(new CommandArgs(KEY, DESCRIPTION, completionHandler, CommandCategory.FILTER), publisher, strategy);
        this.sessionsService = sessionsService;
        this.settingsService = settingsService;
        this.setRegionCommand = setRegionCommand;
    }

    private static InlineKeyboardMarkup initKeyboard() {
        return KeyboardBuilder.buildInlineKeyboard(List.of(
                new CallbackItem(RESET.getKey(), "Сбросить", ALL),
                new CallbackItem(CANCEL_CHANGE.getKey(), "Отмена")
        ), 2);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        messageData.setText("Вы собираетесь *сбросить* настройки поиска.\n" +
                "Это действия *нельзя отменить*. Вы уверены?");

        messageData.setKeyboardMarkup(KEYBOARD);
    }

    @Override
    protected void executeWithParameters(MessageData messageData, ResetFilterFieldType parameter) {
        if (parameter == ALL) {
            settingsService.delete(messageData.getChatId());
        } else {
            if (parameter == LOCATION) {
                setRegionCommand.executeWithParameters(messageData, new LocationSearch(null, null));
            }
        }
        sessionsService.disableInterceptor(messageData.getChatId());
    }
}