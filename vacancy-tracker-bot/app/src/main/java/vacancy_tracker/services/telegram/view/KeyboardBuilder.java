package vacancy_tracker.services.telegram.view;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import vacancy_tracker.model.telegram.view.Describable;
import vacancy_tracker.model.telegram.view.InlineButtonInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class KeyboardBuilder {

    public ReplyKeyboard buildReplyKeyboard(List<Describable> keys, int columnsCount, boolean isDisposable) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        int index = 0;
        while (index < keys.size()) {
            KeyboardRow row = new KeyboardRow();
            for (int i = 0; i < columnsCount && index < keys.size(); i++) {
                var current = keys.get(index++);
                row.add(new KeyboardButton(current.getKey()));
            }
            keyboardRows.add(row);
        }

        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboardRows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(isDisposable)
                .build();
    }

    public InlineKeyboardMarkup buildInlineKeyboard(InlineButtonInfo info) {
        List<InlineKeyboardRow> keyboardRows = new LinkedList<>();
        var row = new InlineKeyboardRow();

        keyboardRows.add(row);
        row.add(createInlineButton(info));
        return new InlineKeyboardMarkup(keyboardRows);
    }

    public InlineKeyboardMarkup buildInlineKeyboard(List<InlineButtonInfo> buttonsInfo, int columnCount) {
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        int index = 0;
        var rowIndex = -1;
        for (var info : buttonsInfo) {
            if (index % columnCount == 0) {
                keyboardRows.add(new InlineKeyboardRow());
                rowIndex++;
            }
            var button = createInlineButton(info);
            keyboardRows.get(rowIndex).add(button);
            index++;
        }
        return new InlineKeyboardMarkup(keyboardRows);
    }

    private InlineKeyboardButton createInlineButton(InlineButtonInfo info) {
        var button = new InlineKeyboardButton(info.text());
        button.setCallbackData(info.callback());
        return button;
    }
}
