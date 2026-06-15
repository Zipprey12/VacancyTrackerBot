package vacancy_tracker.services.telegram.view.formatters.vacancies;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.callback.CompositeCallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;
import vacancy_tracker.services.util.DateUtil;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static vacancy_tracker.model.telegram.callback.VacanciesCallbackKeys.GET_VACANCIES;

@Component
@RequiredArgsConstructor
public class VacancySourcesFormatter {

    public void fill(OutgoingMessage message, List<VacanciesSource> sources, long totalCount, LocalDateTime dateFrom) {
        addText(message, totalCount);
        addKeyboard(message, sources, dateFrom);
    }

    private void addText(OutgoingMessage message, long totalCount) {
        String sb = "Найдено вакансий: *" + totalCount +
                "*\n\nВыберите источник:";
        message.setText(sb);
    }

    private void addKeyboard(OutgoingMessage message, List<VacanciesSource> sources, LocalDateTime dateTime) {
        List<CallbackItem> items;
        if (dateTime == null) {
            items = createItems(sources);
        } else {
            long unixSeconds = DateUtil.toUnixSeconds(dateTime);
            items = createItems(sources, unixSeconds);
        }
        var keyboard = KeyboardBuilder.buildInlineKeyboard(items, 1);
        message.setKeyboardMarkup(keyboard);
    }

    private List<CallbackItem> createItems(List<VacanciesSource> sources) {
        List<CallbackItem> items = new LinkedList<>();
        sources.forEach(s -> {
                    var item = new CallbackItem(GET_VACANCIES.getKey(), s.getName(), s);
                    items.add(item);
                }
        );
        return items;
    }

    private List<CallbackItem> createItems(List<VacanciesSource> sources, long unixSeconds) {
        List<CallbackItem> items = new LinkedList<>();
        sources.forEach(s -> {
                    var item = new CompositeCallbackItem(GET_VACANCIES.getKey(), s.getName(), s, unixSeconds);
                    items.add(item);
                }
        );
        return items;
    }
}
