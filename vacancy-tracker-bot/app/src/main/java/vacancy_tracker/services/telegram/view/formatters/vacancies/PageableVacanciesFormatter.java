package vacancy_tracker.services.telegram.view.formatters.vacancies;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.telegram.callback.CallbackArg;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.callback.CompositeCallbackItem;
import vacancy_tracker.model.telegram.callback.VacanciesCallbackKeys;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.settings.VacanciesShownParams;
import vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder;
import vacancy_tracker.services.util.DateUtil;

import java.util.LinkedList;
import java.util.List;

import static vacancy_tracker.model.telegram.callback.VacancySearchArg.*;

@Component
@RequiredArgsConstructor
public class PageableVacanciesFormatter extends VacanciesMessageFormatter {

    public static final String FOOTER_ITEM_TEXT = "Ко всем источникам";

    private final PaginatedKeyboardBuilder keyboardBuilder;

    public void fill(OutgoingMessage message, VacanciesResponse result, VacanciesShownParams shownParams) {
        var sb = new StringBuilder();

        addSource(sb, result.getSource());
        addHeader(sb, result.getModifiedFrom(), result.getTotal(), result.isCountExact());
        addVacancies(sb, result.getVacancies(), MAX_VACANCIES);
        message.setText(sb.toString());

        if(result.isCanHasOther()){
            var keyboard = createNavigationKeyboard(result, shownParams);
            message.setKeyboardMarkup(keyboard);
        }
    }

    private InlineKeyboardMarkup createNavigationKeyboard(VacanciesResponse response, VacanciesShownParams shownParams) {
        var currentPage = response.getPage();
        List<Object> args = new LinkedList<>();
        var key = VacanciesCallbackKeys.GET_VACANCIES.getKey();

        var source = new CallbackArg(SOURCE.getKey(), response.getSource());
        args.add(source);

        Long castedDate = null;
        if (response.getModifiedFrom() != null) {
            castedDate = DateUtil.toUnixSeconds(response.getModifiedFrom());
            var date = new CallbackArg(FROM.getKey(), castedDate);
            args.add(date);
        }

        var hasAnother = new CallbackArg(HAS_ANOTHER.getKey(), shownParams.isHasAnother());
        args.add(hasAnother);

        List<CallbackItem> footerItems = null;
        if (shownParams.isHasAnother()) {
            var footerItem = createFooterItem(key, castedDate);
            footerItems = List.of(footerItem);
        }
        var totalPages = (int) ((response.getTotal() + MAX_VACANCIES - 1) / MAX_VACANCIES);
        if (totalPages > 0) {
            return keyboardBuilder.createNavigationKeyboard(currentPage, totalPages, args, footerItems);
        }
        return keyboardBuilder.createNavigationKeyboard(currentPage, response.isMore(), args, footerItems);
    }

    private CallbackItem createFooterItem(String key, Long casterDate) {
        if (casterDate == null) {
            return new CallbackItem(key, FOOTER_ITEM_TEXT);
        }
        var arg = new CallbackArg(FROM.getKey(), casterDate);
        return new CompositeCallbackItem(key, FOOTER_ITEM_TEXT, null, arg);
    }
}
