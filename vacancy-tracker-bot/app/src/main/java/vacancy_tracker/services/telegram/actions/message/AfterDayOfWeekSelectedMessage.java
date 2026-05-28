package vacancy_tracker.services.telegram.actions.message;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.SET_TIME;

@Component
public class AfterDayOfWeekSelectedMessage {

    private static final String TEXT = """
            День недели установлен.
            
            Текущее время отправки: *%s*
            """;

    private static final InlineKeyboardMarkup KEYBOARD = buildKeyboard();

    public void format(OutgoingMessage outgoingMessage, NotificationSettings settings) {
        String time = formatTime(settings.getNextNotificationAt());
        outgoingMessage.setText(String.format(TEXT, time));
        outgoingMessage.setKeyboardMarkup(KEYBOARD);
    }

    private String formatTime(LocalDateTime nextNotificationAt) {
        if (nextNotificationAt == null) {
            return "не установлено";
        }
        return nextNotificationAt.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static InlineKeyboardMarkup buildKeyboard() {
        return KeyboardBuilder.buildInlineKeyboard(List.of(
                new CallbackItem(SET_TIME.getKey(), "Изменить время"),
                new CallbackItem(CANCEL_CHANGE.getKey(), "Оставить текущее")
        ), 2);
    }
}