package vacancy_tracker.services.telegram.view.formatters.notification;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.utils.DurationFormatUtil;

import java.time.Duration;
import java.util.List;

import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.SET_WEEKLY;

@Component
public class WeeklyNotificationMessageFormatter {

    private static final String TEXT_BODY = """
            Выберите день недели для отправки уведомления.
            Вы также можете ввести его номер сообщением
            """;

    private static final InlineKeyboardMarkup KEYBOARD = buildKeyboard();

    public void format(OutgoingMessage outgoingMessage, NotificationSettings settings) {
        outgoingMessage.setText(createText(settings.getInterval()));
        outgoingMessage.setKeyboardMarkup(KEYBOARD);
    }

    private String createText(Duration duration) {
        if (duration == null) {
            return "Интервал уведомлений не задан\n" + TEXT_BODY;
        }
        return "Текущий период: " + DurationFormatUtil.format(duration) + "\n"
                + TEXT_BODY;
    }

    private static InlineKeyboardMarkup buildKeyboard() {
        return KeyboardBuilder.buildInlineKeyboard(List.of(
                createItem("Понедельник", 1),
                createItem("Вторник", 2),
                createItem("Среда", 3),
                createItem("Четверг", 4),
                createItem("Пятница", 5),
                createItem("Суббота", 6),
                createItem("Воскресенье", 7),
                new CallbackItem(CANCEL_CHANGE.getKey(), "Отмена")
        ), 2);
    }

    private static CallbackItem createItem(String text, int dayOfWeek) {
        return new CallbackItem(SET_WEEKLY.getKey(), text, dayOfWeek);
    }
}