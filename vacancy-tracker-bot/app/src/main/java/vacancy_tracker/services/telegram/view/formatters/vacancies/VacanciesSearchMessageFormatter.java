package vacancy_tracker.services.telegram.view.formatters.vacancies;

import lombok.experimental.UtilityClass;
import vacancy_tracker.model.telegram.dto.MessageData;

@UtilityClass
public class VacanciesSearchMessageFormatter {

    private static final String MESSAGE_TEXT = "\uD83D\uDD0D Ищу вакансии...";

    public static void fill(MessageData messageData) {
        messageData.setText(MESSAGE_TEXT);
    }
}
