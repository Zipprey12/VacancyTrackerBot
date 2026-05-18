package vacancy_tracker.services.telegram.view.keyboard;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;

import java.util.List;

@Data
@RequiredArgsConstructor
public class KeyboardWithPagination {

    private final PaginatedKeyboardBuilder builder;

    private List<CallbackItem> items;

    public InlineKeyboardMarkup build(int currentPage) {
        return builder.build(items, currentPage);
    }

    public InlineKeyboardMarkup build(int currentPage, String navigationArgs) {
        return builder.build(items, currentPage, navigationArgs);
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}