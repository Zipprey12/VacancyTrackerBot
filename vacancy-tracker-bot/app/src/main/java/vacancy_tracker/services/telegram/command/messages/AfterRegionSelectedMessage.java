package vacancy_tracker.services.telegram.command.messages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AfterRegionSelectedMessage {

    private static final InlineKeyboardMarkup KEYBOARD_BUTTON = initKeyboard();

    private final SendingAndUpdatingMessagePublisher publisher;

    public void publish(MessageData messageData, Region region) {
        OutgoingMessage outgoingMessage = new OutgoingMessage(messageData);
        outgoingMessage.setKeyboardMarkup(KEYBOARD_BUTTON);
        outgoingMessage.setText(createHeader(region));

        publisher.publish(outgoingMessage);
    }

    private String createHeader(Region region) {
        return "Выбран регион: *" + region.getName() + "*.";
    }

    private static InlineKeyboardMarkup initKeyboard() {
        var row1 = initSelectCityRow();
        var row2 = initLocationSelectedRow();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2))
                .build();
    }

    private static InlineKeyboardRow initSelectCityRow() {
        var row = new InlineKeyboardRow();
        var button = new InlineKeyboardButton("Уточнить населенный пункт");

        button.setCallbackData(FilterSettingsCallbackKeys.SET_TOWN.getKey());
        row.add(button);
        return row;
    }

    private static InlineKeyboardRow initLocationSelectedRow() {
        var row = new InlineKeyboardRow();
        var button = new InlineKeyboardButton("Оставить регион");

        button.setCallbackData(FilterSettingsCallbackKeys.CANCEL_FILTER_CHANGE.getKey());
        row.add(button);
        return row;
    }
}