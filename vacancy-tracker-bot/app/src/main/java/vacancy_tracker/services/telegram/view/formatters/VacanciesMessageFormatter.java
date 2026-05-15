package vacancy_tracker.services.telegram.view.formatters;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.entity.Location;
import vacancy_tracker.model.api.entity.Vacancy;

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

    public String format(Vacancy vacancy) {
        return String.format(
                """                    
                        💼 *%s*
                        🏢 %s
                        💰 %s руб.
                        📍 %s
                        🔗 [Подробнее](%s)""",
                vacancy.getName(),
                vacancy.getCompany().getName(),
                getSalaryString(vacancy.getSalaryMin(), vacancy.getSalaryMax()),
                getLocationString(vacancy.getLocation()),
                vacancy.getVacancyUrl()
        );
    }

    public String getSalaryString(int min, int max) {
        if (min == 0) {
            return String.valueOf(max);
        }
        if (max == 0) {
            return String.valueOf(min);
        }
        return min + " - " + max;
    }

    public String getLocationString(Location location) {
        if (location.getTown() != null) {
            return "г. " + location.getTown().getName();
        }
        if (location.getRegion() != null) {
            return location.getRegion().getName();
        }
        return "Не указан";
    }
}
