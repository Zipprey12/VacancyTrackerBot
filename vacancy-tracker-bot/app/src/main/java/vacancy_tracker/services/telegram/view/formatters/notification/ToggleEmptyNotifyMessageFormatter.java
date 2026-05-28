package vacancy_tracker.services.telegram.view.formatters.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.NotificationSettingCallbackKeys.SET_EMPTY_NOTIFY;

@Component
public class ToggleEmptyNotifyMessageFormatter {

    private final OutgoingMessage messageWhenOn = initWhenOn();
    private final OutgoingMessage messageWhenOff = initWhenOff();

    public void format(boolean isEnabled, OutgoingMessage outgoingMessage) {
        if (isEnabled) {
            outgoingMessage.setText(messageWhenOn.getText());
            outgoingMessage.setKeyboardMarkup(messageWhenOn.getKeyboardMarkup());
        } else {
            outgoingMessage.setText(messageWhenOff.getText());
            outgoingMessage.setKeyboardMarkup(messageWhenOff.getKeyboardMarkup());
        }
    }

    private OutgoingMessage initWhenOn() {
        var text = """
                📬 *Уведомления* при отсутствии вакансий *включены*
                
                Вы *будете* получать сообщения, даже если новых вакансий не найдено.""";

        var list = List.of(
                backButton(),
                new CallbackItem(SET_EMPTY_NOTIFY.getKey(), "Не уведомлять", false));

        var keyboard = KeyboardBuilder.buildInlineKeyboard(list, 2);
        var message = new OutgoingMessage();
        message.setText(text);
        message.setKeyboardMarkup(keyboard);
        return message;
    }

    private OutgoingMessage initWhenOff() {
        var text = """
                📭 *Уведомления* при отсутствии вакансий *выключены*
                
                Вы *не будете* получать сообщения, если новых вакансий не найдено.""";

        var list = List.of(
                backButton(),
                new CallbackItem(SET_EMPTY_NOTIFY.getKey(), "Уведомлять", true));

        var keyboard = KeyboardBuilder.buildInlineKeyboard(list, 2);
        var message = new OutgoingMessage();
        message.setText(text);
        message.setKeyboardMarkup(keyboard);
        return message;
    }

    private CallbackItem backButton() {
        return new CallbackItem(CANCEL_CHANGE.getKey(), "Отмена");
    }
}
