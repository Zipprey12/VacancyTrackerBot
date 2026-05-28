package vacancy_tracker.services.telegram.view.formatters.vacancies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.dto.SearchResult;
import vacancy_tracker.model.api.dto.VacanciesResponse;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.view.utils.DatesFormatUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacancyPageFormatter extends VacanciesMessageFormatter {

    private static final String EMPTY_RESULT = "🔍 Новых вакансий не найдено";

    public void fillMessage(OutgoingMessage message, SearchResult result) {
        var responses = result.getVacanciesResponses();
        if (responses == null || responses.isEmpty()) {
            fillEmptyResult(message, result);
            return;
        }
        var stringBuilder = new StringBuilder();
        addHeader(stringBuilder, result);

        int currentCount = 0;
        for (var response : responses) {
            if (currentCount >= MAX_VACANCIES) {
                break;
            }
            add(stringBuilder, response, currentCount);
            currentCount += response.getVacancies().size();
        }
        message.setText(stringBuilder.toString());
    }

    private void fillEmptyResult(OutgoingMessage message, SearchResult result) {
        var from = result.getModifiedFrom();
        if (from == null) {
            message.setText(EMPTY_RESULT);
            return;
        }
        var text = "Вакансий новее " + DatesFormatUtil.formatDateTime(from) + " не найдено";
        message.setText(text);
    }

    private void add(StringBuilder sb, VacanciesResponse response, int currentCount) {
        if (response.isEmpty()) {
            return;
        }
        addSource(sb, response.getSource());
        addVacancies(sb, response.getVacancies(), MAX_VACANCIES - currentCount);
        sb.append("\n\n");
    }
}
