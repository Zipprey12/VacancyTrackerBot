package vacancy_tracker.services.telegram.view.keyboard;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.callback.CommonCallbackKeys;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;

import java.util.Collections;
import java.util.List;

public class PaginatedKeyboardBuilder extends AdvancedKeyboardBuilder {

    private static final String PREV_TEXT = "◀️ Назад";
    private static final String NEXT_TEXT = "Вперед ▶️";

    @Getter
    private final PaginationCallbackParser castedParser;

    public PaginatedKeyboardBuilder(PaginationCallbackParser parser) {
        super(parser);
        this.castedParser = parser;
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items, int currentPage) {
        return build(items, currentPage, getItemsPerPage(), null);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items, int currentPage, Object navigationArgs) {
        return build(items, currentPage, getItemsPerPage(), navigationArgs == null ? null : Collections.singletonList(navigationArgs));
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items, int currentPage, List<Object> navigationArgs) {
        return build(items, currentPage, getItemsPerPage(), navigationArgs);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items, int currentPage, int itemsPerPage) {
        return build(items, currentPage, itemsPerPage, null);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items,
                                      int currentPage,
                                      int itemsPerPage,
                                      List<Object> navigationArgs) {
        performCheckItems(items);
        int totalPages = calculateTotalPages(items.size(), itemsPerPage);
        List<InlineKeyboardRow> rows = createPageItemRows(items, currentPage, itemsPerPage);

        if (totalPages > 1) {
            rows.add(createNavigationRow(currentPage, totalPages, navigationArgs));
        }
        return new InlineKeyboardMarkup(rows);
    }

    private List<InlineKeyboardRow> createPageItemRows(List<CallbackItem> items,
                                                       int currentPage,
                                                       int itemsPerPage) {
        int from = currentPage * itemsPerPage;
        int to = Math.min(from + itemsPerPage, items.size());
        return createItemsRow(items.subList(from, to));
    }

    private InlineKeyboardRow createNavigationRow(int currentPage, int totalPages, List<Object> args) {
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
        var data = castedParser.createSelectPageCallback(targetPage, args);

        button.setCallbackData(data);
        return button;
    }

    private static InlineKeyboardButton createPageIndicator(int currentPage, int totalPages) {
        InlineKeyboardButton indicator = new InlineKeyboardButton(
                (currentPage + 1) + " / " + totalPages
        );
        indicator.setCallbackData(CommonCallbackKeys.IGNORE.getKey());
        return indicator;
    }

    private static int calculateTotalPages(int totalItems, int itemsPerPage) {
        if (totalItems == 0) return 1;
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }
}