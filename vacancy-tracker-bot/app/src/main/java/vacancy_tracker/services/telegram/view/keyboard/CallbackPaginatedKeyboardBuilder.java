package vacancy_tracker.services.telegram.view.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.services.telegram.callback.parsers.PaginationCallbackParser;

import java.util.List;

import static vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder.calculateTotalPages;
import static vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder.createFooterItemsRow;

public class CallbackPaginatedKeyboardBuilder extends AdvancedKeyboardBuilder {

    private final PaginatedKeyboardBuilder paginationBuilder;

    public CallbackPaginatedKeyboardBuilder(PaginationCallbackParser parser) {
        super(parser);
        this.paginationBuilder = new PaginatedKeyboardBuilder(parser);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items, int currentPage) {
        return build(items, currentPage, getItemsPerPage(), null, null);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items, int currentPage, List<Object> navigationArgs) {
        return build(items, currentPage, getItemsPerPage(), navigationArgs, null);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items, int currentPage, int itemsPerPage) {
        return build(items, currentPage, itemsPerPage, null, null);
    }

    public InlineKeyboardMarkup build(List<CallbackItem> items,
                                      int currentPage,
                                      int itemsPerPage,
                                      List<Object> navigationArgs,
                                      List<CallbackItem> itemsAfterNavigation) {
        performCheckItems(items);
        int totalPages = calculateTotalPages(items.size(), itemsPerPage);
        List<InlineKeyboardRow> rows = createPageItemRows(items, currentPage, itemsPerPage);

        if (totalPages > 1) {
            rows.add(paginationBuilder.createNavigationRow(currentPage, totalPages, navigationArgs));
        }
        if (itemsAfterNavigation != null && !itemsAfterNavigation.isEmpty()) {
            rows.add(createFooterItemsRow(itemsAfterNavigation));
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
}