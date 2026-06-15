package vacancy_tracker.services.telegram.view.keyboard;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import vacancy_tracker.model.telegram.callback.CallbackArgs;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.callback.CommonCallbacks;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;

import java.util.LinkedList;
import java.util.List;

public class PaginatedKeyboardBuilder extends AdvancedKeyboardBuilder {

    protected static final String PREV_TEXT = "◀️ Назад";
    protected static final String NEXT_TEXT = "Вперед ▶️";

    @Getter
    private final PaginationCallbackParser castedParser;

    public PaginatedKeyboardBuilder(PaginationCallbackParser parser) {
        super(parser);
        castedParser = parser;
    }

    public static InlineKeyboardRow createFooterItemsRow(List<CallbackItem> callbackItems) {
        var buttons = new LinkedList<InlineKeyboardButton>();
        callbackItems.forEach(i -> buttons.add(KeyboardBuilder.createInlineButton(i)));
        return new InlineKeyboardRow(buttons);
    }

    public static InlineKeyboardButton createPageIndicator(int currentPage, int totalPages) {
        InlineKeyboardButton indicator = new InlineKeyboardButton(
                (currentPage + 1) + " / " + totalPages
        );
        indicator.setCallbackData(CommonCallbacks.IGNORE.getKey());
        return indicator;
    }

    public static int calculateTotalPages(int totalItems, int itemsPerPage) {
        if (totalItems == 0) return 1;
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    public InlineKeyboardMarkup createNavigationKeyboard(int currentPage, int totalPages, List<Object> args) {
        var row = List.of(createNavigationRow(currentPage, totalPages, args));
        return new InlineKeyboardMarkup(row);
    }

    public InlineKeyboardMarkup createNavigationKeyboard(int currentPage, int totalPages,
                                                         List<Object> args, List<CallbackItem> footerItems) {
        List<InlineKeyboardRow> rows;
        if (totalPages <= 1) {
            rows = List.of(createFooterItemsRow(footerItems));
        } else {
            rows = List.of(
                    createNavigationRow(currentPage, totalPages, args),
                    createFooterItemsRow(footerItems));
        }
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardRow createNavigationRow(int currentPage, int totalPages, List<Object> args) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        if (currentPage > 0) {
            row.add(createNavigationButton(PREV_TEXT, currentPage - 1, args));
        }

        row.add(createPageIndicator(currentPage, totalPages));
        if (currentPage < totalPages - 1) {
            row.add(createNavigationButton(NEXT_TEXT, currentPage + 1, args));
        }
        return row;
    }

    private InlineKeyboardButton createNavigationButton(String text, int targetPage, List<Object> args) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);

        var casted = new CallbackArgs(args);
        var data = getCastedParser().createSelectPageCallback(targetPage, casted);

        button.setCallbackData(data);
        return button;
    }
}