package vacancy_tracker.services.telegram.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.services.telegram.message.MessageEditor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AfterRegionSelectedMessage {

    private static final InlineKeyboardMarkup KEYBOARD_BUTTON = initKeyboard();

    private final MessageEditor editor;

    public void update(MessageData messageData, Region region) {
        var messageId = messageData.getMessageId();
        var chatId = messageData.getChatId();

        editor.edit(createHeader(region), chatId, messageId);
        editor.edit(KEYBOARD_BUTTON, chatId, messageId);
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

        button.setCallbackData(FilterSettingsCallbackKeys.SELECT_TOWN.getKey());
        row.add(button);
        return row;
    }

    private static InlineKeyboardRow initLocationSelectedRow() {
        var row = new InlineKeyboardRow();
        var button = new InlineKeyboardButton("Оставить регион");

        button.setCallbackData(FilterSettingsCallbackKeys.LOCATION_SELECTED.getKey());
        row.add(button);
        return row;
    }
}