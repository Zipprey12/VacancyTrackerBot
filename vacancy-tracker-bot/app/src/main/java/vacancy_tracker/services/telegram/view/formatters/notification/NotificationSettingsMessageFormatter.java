package vacancy_tracker.services.telegram.view.formatters.notification;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.NotificationSettings;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.utils.DurationFormatUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.*;

@Component
public class NotificationSettingsMessageFormatter {

    public void fill(OutgoingMessage outgoingMessage, NotificationSettings settings) {
        outgoingMessage.setText(generateText(settings));
        outgoingMessage.setKeyboardMarkup(generateKeyboard(settings));
    }

    public String generateHeader(NotificationSettings settings) {
        if (!settings.isEnabled()) {
            return """
                    🔕 *Уведомления отключены*
                    
                    Вы не будете получать оповещения о новых вакансиях.
                    """;
        }
        return "🔔 *Уведомления включены*\n\n" +
                "⏰ *Следующее оповещение:* " +
                formatNextTime(settings.getNextNotificationAt()) +
                "\n🔄 *Интервал:* " +
                DurationFormatUtil.format(settings.getInterval()) +
                "\n📭 *Уведомлять при отсутствии вакансий:* " +
                (settings.isNotifyWhenVacanciesNotFound() ? "да" : "нет") + "\n";
    }

    public String generateText(NotificationSettings settings) {
        if (!settings.isEnabled()) {
            return generateHeader(settings) + "\nИспользуйте кнопки ниже, чтобы включить их.";
        }
        return generateHeader(settings);
    }

    public InlineKeyboardMarkup generateOnOffButton(boolean isEnabled) {
        return KeyboardBuilder.buildInlineKeyboard(createOnOffCallback(isEnabled));
    }

    private CallbackItem createOnOffCallback(boolean isEnabled) {
        return isEnabled
                ? new CallbackItem(ENABLED.getKey(), "Отключить уведомления", false)
                : new CallbackItem(ENABLED.getKey(), "Включить уведомления", true);
    }

    private InlineKeyboardMarkup generateKeyboard(NotificationSettings settings) {
        List<CallbackItem> list;
        var toggleEnabled = createOnOffCallback(settings.isEnabled());
        var interval = new CallbackItem(SET_INTERVAL.getKey(), "Изменить интервал");

        if (settings.isEnabled()) {
            var toggleEmptyNotify = settings.isNotifyWhenVacanciesNotFound()
                    ? new CallbackItem(SET_EMPTY_NOTIFY.getKey(), "Не уведомлять при отсутствии вакансий", false)
                    : new CallbackItem(SET_EMPTY_NOTIFY.getKey(), "Уведомлять при отсутствии вакансий", true);
            list = List.of(toggleEnabled, interval, toggleEmptyNotify);
        } else {
            list = List.of(toggleEnabled, interval);
        }
        return KeyboardBuilder.buildInlineKeyboard(list, 2);
    }

    private String formatNextTime(LocalDateTime time) {
        if (time == null) return "не установлено";
        return time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
