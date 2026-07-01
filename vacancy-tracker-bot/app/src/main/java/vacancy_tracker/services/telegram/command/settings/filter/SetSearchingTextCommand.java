package vacancy_tracker.services.telegram.command.settings.filter;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.command.CommandCategory;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.TextInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.CommonCallbacks.NULL;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_TEXT;

@Component
public class SetSearchingTextCommand extends InputInterceptingCommand<String> {

    public static final String KEY = "/text";
    public static final String DESCRIPTION = "Текст вакансии";
    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    private final SearchFiltersService settingsService;

    public SetSearchingTextCommand(SendingAndUpdatingMessagePublisher publisher,
                                   SessionsService sessionsService,
                                   FiltersChangingCompletionHandler handler,
                                   SearchFiltersService settingsService,
                                   SequentialAsyncExecutionStrategy strategy) {
        super(new CommandArgs(KEY, DESCRIPTION, handler, CommandCategory.FILTER), publisher,
                new TextInterceptor(), sessionsService, strategy);
        this.settingsService = settingsService;
    }

    private static InlineKeyboardMarkup initKeyboard() {
        var items = List.of(
                new CallbackItem(CANCEL_CHANGE.getKey(), "Оставить текущий"),
                new CallbackItem(SET_TEXT.getKey(), "Сбросить", NULL.getKey())
        );
        return KeyboardBuilder.buildInlineKeyboard(items, 2);
    }

    @Override
    protected void executeWithParameters(MessageData messageData, String parameter) {
        var chatId = messageData.getChatId();
        var filters = settingsService.get(chatId);
        filters.setText(parameter);
        settingsService.save(chatId, filters);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var currentText = settingsService.get(messageData.getChatId()).getText();

        messageData.setText(createText(currentText));
        messageData.setKeyboardMarkup(KEYBOARD);
    }

    private String createText(String currentText) {
        var secondPart = "Вы можете установить новый, отправив его сообщением";

        if (currentText == null) {
            return "Сейчас у вас не задан текст для поиска\n" +
                    secondPart;
        }
        return currentText.isBlank() ? "Текст для поиска не задан" :
                ("Текущий текст для поиска: *" + currentText + "*.\n")
                        + secondPart;
    }
}