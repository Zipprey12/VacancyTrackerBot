package vacancy_tracker.services.telegram.command.settings;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersInterceptingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.SearchTextInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.CommonCallbackKeys.NULL;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_FILTER_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_TEXT;

@Component
public class SetSearchingTextCommand extends InputInterceptingCommand {

    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    private final SettingsService settingsService;

    public SetSearchingTextCommand(SendingAndUpdatingMessagePublisher publisher,
                                   SessionsService sessionsService,
                                   FiltersInterceptingCompletionHandler handler,
                                   SettingsService settingsService) {
        super("/set_search_text",
                "Установить текст для поиска:",
                publisher,
                handler,
                new SearchTextInterceptor(publisher.getSender(), sessionsService, settingsService),
                sessionsService
        );
        this.settingsService = settingsService;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var currentText = settingsService.getFilters(messageData.getChatId()).getText();

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
                new CallbackItem(CANCEL_FILTER_CHANGE.getKey(), "Оставить текущий"),
                new CallbackItem(NULL.getKey(), SET_TEXT.getKey(), "Сбросить")
        );
        return KeyboardBuilder.buildInlineKeyboard(items, 2);
    }
}