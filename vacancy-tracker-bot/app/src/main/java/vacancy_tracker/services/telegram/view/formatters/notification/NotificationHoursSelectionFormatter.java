package vacancy_tracker.services.telegram.view.formatters.notification;

import lombok.RequiredArgsConstructor;
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
import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.SET_HOURS;

@Component
@RequiredArgsConstructor
public class NotificationHoursSelectionFormatter {

    private static final String TEXT_BODY = """
            Выберите вариант или отправьте количество часов сообщением.
            *Например*: `3:20` - уведомление через каждые _3 часа 20 минут_
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
                createItem("4 часа", 4),
                createItem("6 часов", 6),
                createItem("8 часов", 8),
                createItem("12 часов", 12),
                new CallbackItem(CANCEL_CHANGE.getKey(), "Оставить текущий")

        ), 2);
    }

    private static CallbackItem createItem(String text, int count) {
        return new CallbackItem(SET_HOURS.getKey(), text, count);
    }
}
