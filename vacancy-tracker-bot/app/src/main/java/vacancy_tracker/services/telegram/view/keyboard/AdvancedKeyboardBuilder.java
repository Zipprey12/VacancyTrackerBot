package vacancy_tracker.services.telegram.view.keyboard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.services.telegram.callback.parsers.CallbackParser;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AdvancedKeyboardBuilder {

    public static final int DEFAULT_ITEMS_PER_PAGE = 10;

    private final CallbackParser parser;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;

    public InlineKeyboardMarkup build(List<CallbackItem> items) {
        performCheckItems(items);
        return new InlineKeyboardMarkup(createItemsRow(items));
    }

    protected void performCheckItems(List<CallbackItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("items не могут быть null при создании клавиатуры");
        }
    }

    protected List<InlineKeyboardRow> createItemsRow(List<CallbackItem> items) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (CallbackItem item : items) {
            rows.add(createItemRow(item));
        }
        return rows;
    }

    protected InlineKeyboardRow createItemRow(CallbackItem item) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        InlineKeyboardButton button = new InlineKeyboardButton(item.getDisplayedName());
        var data = parser.createSelectItemCallback(item);

        button.setCallbackData(data);
        row.add(button);
        return row;
    }
}
