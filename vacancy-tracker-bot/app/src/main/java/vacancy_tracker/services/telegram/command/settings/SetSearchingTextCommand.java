package vacancy_tracker.services.telegram.command.settings;

import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.RESET_TEXT;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_FILTER_CHANGE;

public class SetSearchingTextCommand extends SearchFiltersCommand {

    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    private final SettingsService settingsService;

    public SetSearchingTextCommand(MessageSender sender,
                                   MessageEditor editor,
                                   SessionsService sessionsService,
                                   InputInterceptor inputInterceptor,
                                   ApplicationEventPublisher eventPublisher,
                                   SettingsService settingsService) {
        super("/set_search_text",
                "Установить текст для поиска:",
                sender,
                editor,
                sessionsService,
                inputInterceptor,
                eventPublisher);
        this.settingsService = settingsService;

        setMarkSignificantAfterExecution(true);
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
                new CallbackItem(RESET_TEXT.getKey(), "Сбросить")
        );
        return KeyboardBuilder.buildInlineKeyboard(items, 2);
    }
}