package vacancy_tracker.services.telegram.view;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.vacancy.Vacancy;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VacanciesMessageFormatter {

    public String format(List<Vacancy> vacancies) {
        return "`Пока что я просто вывожу 10 последних вакансий!`\n\n" +
                vacancies.stream()
                .map(this::format)
                .collect(Collectors.joining("\n\n"));
    }

    private String format(Vacancy vacancy) {
        return String.format(
                """                    
                        🏢 *%s*
                        💼 %s
                        💰 %s RUB
                        📍 %s
                        🔗 [Подробнее](%s)""",
                vacancy.getCompany().getName(),
                vacancy.getName(),
                getSalaryString(vacancy.getSalaryMin(), vacancy.getSalaryMax()),
                vacancy.getRegionName() != null ? vacancy.getRegionName() : "Не указан",
                vacancy.getVacancyUrl()
        );
    }

    private String getSalaryString(int min, int max) {
        if (min == 0) {
            return String.valueOf(max);
        }
        if (max == 0) {
            return String.valueOf(min);
        }
        return min + " - " + max;
    }
}
