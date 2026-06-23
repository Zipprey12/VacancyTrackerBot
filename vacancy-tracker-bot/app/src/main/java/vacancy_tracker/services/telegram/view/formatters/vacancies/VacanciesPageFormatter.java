package vacancy_tracker.services.telegram.view.formatters.vacancies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.model.search.VacanciesResponse;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.view.utils.DatesFormatUtil;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacanciesPageFormatter extends VacanciesMessageFormatter {

    private static final String EMPTY_RESULT = "🔍 Не удалось найти вакансии, подходящие под запрос";

    public void fillMessage(OutgoingMessage message, SearchResult result) {
        var responses = result.getVacanciesResponses();
        if (responses == null || responses.isEmpty() || result.getNotEmptySources().isEmpty()) {
            fillEmptyResult(message, result.getModifiedFrom());
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

    public void fillMessage(OutgoingMessage message, VacanciesResponse response) {
        if (response.isEmpty()) {
            fillEmptyResult(message, response.getModifiedFrom());
            return;
        }
        var sb = new StringBuilder();
        addHeader(sb, response.getModifiedFrom(), response.getTotal(), response.isCountExact());
        add(sb, response, response.getVacancies().size());
        message.setText(sb.toString());
    }

    private void fillEmptyResult(OutgoingMessage message, LocalDateTime from) {
        if (from == null) {
            message.setText(EMPTY_RESULT);
            return;
        }
        var text = "Не найдено вакансий, подходящих под запрос и созданных после " + DatesFormatUtil.formatDateTime(from);
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
