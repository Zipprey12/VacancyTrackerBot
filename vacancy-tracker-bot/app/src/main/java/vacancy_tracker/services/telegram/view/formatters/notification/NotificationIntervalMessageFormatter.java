package vacancy_tracker.services.telegram.view.formatters.notification;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.IntervalType.*;
import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.SET_INTERVAL;

@Component
public class NotificationIntervalMessageFormatter {

    private static final InlineKeyboardMarkup KEYBOARD = buildKeyboard();

    public void fill(OutgoingMessage message, NotificationSettings settings) {
        message.setText(buildText(settings));
        message.setKeyboardMarkup(KEYBOARD);
    }

    private static String buildText(NotificationSettings settings) {
        var header = "🔔 *Настройка уведомлений*\n\n";
        var current = "Текущий период: *" + formatInterval(settings) + "*\n\n";
        var prompt = "Выберите как часто получать уведомления о новых вакансиях:";
        return header + current + prompt;
    }

    private static String formatInterval(NotificationSettings settings) {
        if (!settings.isEnabled() || settings.getInterval() == null) {
            return "отключены";
        }
        var hours = settings.getInterval().toHours();
        if (hours == 24) return "раз в день";
        if (hours % 168 == 0) return "раз в неделю";
        if (hours > 24) return "раз в " + (hours / 24) + " дн.";
        return "каждые " + hours + " ч.";
    }

    private static InlineKeyboardMarkup buildKeyboard() {
        return KeyboardBuilder.buildInlineKeyboard(List.of(
                new CallbackItem(SET_INTERVAL.getKey(), "Задать время", HOURS.getKey()),
                new CallbackItem(SET_INTERVAL.getKey(), "Раз в день", DAILY.getKey()),
                new CallbackItem(SET_INTERVAL.getKey(), "Раз в неделю", WEEKLY.getKey()),
                new CallbackItem(CANCEL_CHANGE.getKey(), "Отмена")
        ), 2);
    }
}
