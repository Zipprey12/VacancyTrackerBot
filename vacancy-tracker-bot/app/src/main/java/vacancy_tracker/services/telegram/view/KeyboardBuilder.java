package vacancy_tracker.services.telegram.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import vacancy_tracker.model.telegram.view.CallBackDataProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KeyboardBuilder {

    public InlineKeyboardMarkup buildInlineKeyboard(List<? extends CallBackDataProvider> dataProviders, int columnCount) {
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        int index = 0;
        var rowIndex = -1;
        for (var dataProvider : dataProviders) {
            if (index % columnCount == 0) {
                keyboardRows.add(new InlineKeyboardRow());
                rowIndex++;
            }
            var button = createInlineButton(dataProvider);
            keyboardRows.get(rowIndex).add(button);
            index++;
        }
        return new InlineKeyboardMarkup(keyboardRows);
    }

    private InlineKeyboardButton createInlineButton(CallBackDataProvider dataProvider) {
        var button = new InlineKeyboardButton(dataProvider.getText());
        button.setCallbackData(dataProvider.getCallback());
        return button;
    }
}
