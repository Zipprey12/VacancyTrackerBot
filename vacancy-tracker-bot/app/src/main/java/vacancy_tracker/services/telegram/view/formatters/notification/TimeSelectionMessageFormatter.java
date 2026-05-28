package vacancy_tracker.services.telegram.view.formatters.notification;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.SET_TIME;
import static vacancy_tracker.services.DateUtil.ONE_DAY_SECONDS;
import static vacancy_tracker.services.DateUtil.ONE_WEEK_SECONDS;

@Component
public class TimeSelectionMessageFormatter {

    private static final String TEXT_BODY = """
            Выберите время отправки уведомления.
            Или отправьте сообщением, например: `9:00` или `21 30`
            """;

    private static final InlineKeyboardMarkup KEYBOARD = buildKeyboard();

    public void format(OutgoingMessage outgoingMessage, NotificationSettings settings) {
        outgoingMessage.setText(createText(settings));
        outgoingMessage.setKeyboardMarkup(KEYBOARD);
    }

    private static InlineKeyboardMarkup buildKeyboard() {
        return KeyboardBuilder.buildInlineKeyboard(List.of(
                createItem("06:00", 6),
                createItem("09:00", 9),
                createItem("12:00", 12),
                createItem("15:00", 15),
                createItem("18:00", 18),
                createItem("21:00", 21),
                new CallbackItem(CANCEL_CHANGE.getKey(), "Оставить текущее")
        ), 2);
    }

    private static CallbackItem createItem(String text, int hours) {
        return new CallbackItem(SET_TIME.getKey(), text, hours);
    }

    private String createText(NotificationSettings settings) {
        var interval = settings.getInterval();
        if (interval == null) {
            return "Время отправки не задано\n" + TEXT_BODY;
        }

        long seconds = interval.getSeconds();
        if (seconds == ONE_DAY_SECONDS || seconds == ONE_WEEK_SECONDS) {
            var next = settings.getNextNotificationAt();
            if (next != null) {
                String time = next.format(DateTimeFormatter.ofPattern("HH:mm"));
                return String.format("Текущее время отправки: %s%n%s", time, TEXT_BODY);
            }
        }

        return TEXT_BODY;
    }
}
