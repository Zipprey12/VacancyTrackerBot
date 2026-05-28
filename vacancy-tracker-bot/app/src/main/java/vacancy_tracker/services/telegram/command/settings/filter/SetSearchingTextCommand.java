package vacancy_tracker.services.telegram.command.settings.filter;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.TextInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.CommonCallbacks.NULL;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_TEXT;

@Component
public class SetSearchingTextCommand extends InputInterceptingCommand<String> {

    public static final String KEY = "/set_search_text";
    public static final String DESCRIPTION = "Установить текст для поиска";
    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    private final SearchFiltersService settingsService;

    public SetSearchingTextCommand(SendingAndUpdatingMessagePublisher publisher,
                                   SessionsService sessionsService,
                                   FiltersChangingCompletionHandler handler,
                                   SearchFiltersService settingsService) {
        super(KEY,
                DESCRIPTION,
                publisher,
                handler,
                new TextInterceptor(),
                sessionsService
        );
        this.settingsService = settingsService;
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
        return "Текущий текст для поиска: *" + currentText + "*.\n" + secondPart;
    }

    private static InlineKeyboardMarkup initKeyboard() {
        var items = List.of(
                new CallbackItem(CANCEL_CHANGE.getKey(), "Оставить текущий"),
                new CallbackItem(SET_TEXT.getKey(), "Сбросить", NULL.getKey())
        );
        return KeyboardBuilder.buildInlineKeyboard(items, 2);
    }
}