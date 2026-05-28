package vacancy_tracker.services.telegram.view.formatters.vacancies;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.api.RequestType;
import vacancy_tracker.model.api.dto.VacanciesResponse;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.callback.CompositeCallbackItem;
import vacancy_tracker.model.telegram.callback.VacanciesCallbackKeys;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.DateUtil;
import vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PageableVacanciesFormatter extends VacanciesMessageFormatter {

    public static final String FOOTER_ITEM_TEXT = "Ко всем источникам";

    private final PaginatedKeyboardBuilder keyboardBuilder;

    public void fill(OutgoingMessage message, VacanciesResponse result, RequestType requestType, boolean showBackButton) {
        var sb = new StringBuilder();

        addSource(sb, result.getSource());
        addHeader(sb, requestType, result.getModifiedFrom(), result.getTotal());
        addVacancies(sb, result.getVacancies(), MAX_VACANCIES);
        var keyboard = createNavigationKeyboard(result, showBackButton);
        message.setKeyboardMarkup(keyboard);
        message.setText(sb.toString());
    }

    private InlineKeyboardMarkup createNavigationKeyboard(VacanciesResponse response, boolean showBack) {
        var currentPage = (int) (response.getOffset() / MAX_VACANCIES);
        var totalPages = (int) ((response.getTotal() + MAX_VACANCIES - 1) / MAX_VACANCIES);

        List<Object> args;
        var key = VacanciesCallbackKeys.GET_VACANCIES.getKey();

        Long castedDate = null;
        if (response.getModifiedFrom() == null) {
            args = List.of(response.getSource());
        } else {
            castedDate = DateUtil.toUnixSeconds(response.getModifiedFrom());
            args = List.of(response.getSource(), castedDate);
        }

        if (showBack) {
            var footerItem = createFooterItem(key, castedDate);
            return keyboardBuilder.createNavigationKeyboard(currentPage, totalPages, args, List.of(footerItem));
        }
        return keyboardBuilder.createNavigationKeyboard(currentPage, totalPages, args);
    }

    private CallbackItem createFooterItem(String key, Long casterDate) {
        if (casterDate == null) {
            return new CallbackItem(key, FOOTER_ITEM_TEXT);
        }
        return new CompositeCallbackItem(key, FOOTER_ITEM_TEXT, null, casterDate);
    }
}
