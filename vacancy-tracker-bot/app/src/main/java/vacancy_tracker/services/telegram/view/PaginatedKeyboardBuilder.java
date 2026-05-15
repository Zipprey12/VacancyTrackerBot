package vacancy_tracker.services.telegram.view;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.callback.CommonCallbackKeys;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;

import java.util.ArrayList;
import java.util.List;


public class PaginatedKeyboardBuilder {

    private static final int DEFAULT_ITEMS_PER_PAGE = 10;
    private static final String PREV_TEXT = "◀️ Назад";
    private static final String NEXT_TEXT = "Вперед ▶️";

    @Getter
    private final PaginationCallbackParser parser;

    @Getter
    @Setter
    private List<CallbackItem> defaultItems;

    @Setter(AccessLevel.PROTECTED)
    private int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;

    public PaginatedKeyboardBuilder(PaginationCallbackParser parser) {
        this.parser = parser;
    }

    public InlineKeyboardMarkup build(int currentPage) {
        if (defaultItems == null) {
            throw new IllegalArgumentException("defaultItems is null");
        }
        return build(defaultItems, currentPage, itemsPerPage);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items, int currentPage) {
        return build(items, currentPage, itemsPerPage);
    }

    public InlineKeyboardMarkup build(int currentPage, String navigationArgs) {
        if (defaultItems == null) {
            throw new IllegalArgumentException("defaultItems is null");
        }
        return build(defaultItems, currentPage, itemsPerPage, navigationArgs);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items, int currentPage, String navigationArgs) {
        return build(items, currentPage, itemsPerPage, navigationArgs);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items,
                                      int currentPage,
                                      int itemsPerPage,
                                      String navigationArgs) {
        performCheckItems(items);
        int totalPages = calculateTotalPages(items.size(), itemsPerPage);
        var rows = createPageItemRows(items, currentPage, itemsPerPage);

        if (totalPages > 1) {
            rows.add(createNavigationRow(currentPage, totalPages, navigationArgs));
        }
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items,
                                      int currentPage,
                                      int itemsPerPage) {
        performCheckItems(items);
        int totalPages = calculateTotalPages(items.size(), itemsPerPage);
        var rows = createPageItemRows(items, currentPage, itemsPerPage);

        if (totalPages > 1) {
            rows.add(createNavigationRow(currentPage, totalPages));
        }
        return new InlineKeyboardMarkup(rows);
    }

    private void performCheckItems(List<CallbackItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("items не могут быть null при создании клавиатуры");
        }
    }

    private List<InlineKeyboardRow> createPageItemRows(List<CallbackItem> items,
                                                       int currentPage,
                                                       int itemsPerPage) {
        int from = currentPage * itemsPerPage;
        int to = Math.min(from + itemsPerPage, items.size());

        List<CallbackItem> pageItems = items.subList(from, to);
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (CallbackItem item : pageItems) {
            rows.add(createItemRow(item));
        }
        return rows;
    }

    private InlineKeyboardRow createItemRow(CallbackItem item) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        InlineKeyboardButton button = new InlineKeyboardButton(item.displayedName());
        var data = parser.createSelectItemCallback(item);

        button.setCallbackData(data);
        row.add(button);
        return row;
    }

    private InlineKeyboardRow createNavigationRow(int currentPage, int totalPages) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        if (currentPage > 0) {
            row.add(createNavigationButton(PREV_TEXT, currentPage - 1));
        }

        row.add(createPageIndicator(currentPage, totalPages));
        if (currentPage < totalPages - 1) {
            row.add(createNavigationButton(NEXT_TEXT, currentPage + 1));
        }
        return row;
    }

    private InlineKeyboardRow createNavigationRow(int currentPage, int totalPages, String args) {
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

    private InlineKeyboardButton createNavigationButton(String text, int targetPage) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        var data = parser.createSelectPageCallback(targetPage);

        button.setCallbackData(data);
        return button;
    }

    private InlineKeyboardButton createNavigationButton(String text, int targetPage, String args) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        var data = parser.createSelectPageCallback(targetPage, args);

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
